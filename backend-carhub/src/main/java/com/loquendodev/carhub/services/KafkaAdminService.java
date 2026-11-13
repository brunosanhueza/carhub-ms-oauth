package com.loquendodev.carhub.services;

import com.loquendodev.carhub.models.dto.*;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.TopicConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Servicio de administración dinámica de Apache Kafka:
 * Permite crear, listar, inspeccionar, modificar y eliminar tópicos,
 * además de auditar y monitorear los Consumer Groups del clúster.
 */
@Service
public class KafkaAdminService {

    private static final Logger log = LoggerFactory.getLogger(KafkaAdminService.class);

    private final AdminClient adminClient;

    @Autowired
    public KafkaAdminService(AdminClient adminClient) {
        this.adminClient = adminClient;
    }

    /**
     * Declara un nuevo tópico con particiones, factor de replicación y configuraciones de retención.
     */
    public String crearTopico(TopicRequestDto dto) {
        try {
            Map<String, String> topicConfigs = new HashMap<>();
            if (dto.getRetentionMs() != null) {
                topicConfigs.put(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(dto.getRetentionMs()));
            }
            if (dto.getCleanupPolicy() != null) {
                topicConfigs.put(TopicConfig.CLEANUP_POLICY_CONFIG, dto.getCleanupPolicy());
            }

            NewTopic newTopic = new NewTopic(dto.getName(), dto.getPartitions(), dto.getReplicationFactor());
            newTopic.configs(topicConfigs);

            CreateTopicsResult result = adminClient.createTopics(Collections.singletonList(newTopic));
            result.all().get(10, TimeUnit.SECONDS);

            log.info("✅ [KAFKA ADMIN] Tópico '{}' creado con {} particiones y réplica {}",
                    dto.getName(), dto.getPartitions(), dto.getReplicationFactor());
            return "Tópico '" + dto.getName() + "' creado exitosamente en el clúster Kafka.";
        } catch (Exception e) {
            log.error("❌ [KAFKA ADMIN] Error al crear tópico '{}': {}", dto.getName(), e.getMessage());
            throw new RuntimeException("Error al crear tópico en Kafka: " + e.getMessage(), e);
        }
    }

    /**
     * Lista todos los tópicos del clúster (excluyendo o incluyendo internos).
     */
    public List<TopicResponseDto> listarTopicos() {
        try {
            ListTopicsOptions options = new ListTopicsOptions().listInternal(false);
            Set<String> topicNames = adminClient.listTopics(options).names().get(10, TimeUnit.SECONDS);

            if (topicNames.isEmpty()) {
                return Collections.emptyList();
            }

            Map<String, TopicDescription> descriptions = adminClient.describeTopics(topicNames).allTopicNames().get(10, TimeUnit.SECONDS);

            List<TopicResponseDto> responseList = new ArrayList<>();
            for (Map.Entry<String, TopicDescription> entry : descriptions.entrySet()) {
                String name = entry.getKey();
                TopicDescription desc = entry.getValue();

                int partitions = desc.partitions().size();
                int repFactor = partitions > 0 ? desc.partitions().get(0).replicas().size() : 1;

                responseList.add(TopicResponseDto.builder()
                        .name(name)
                        .partitions(partitions)
                        .replicationFactor(repFactor)
                        .internal(desc.isInternal())
                        .build());
            }

            return responseList;
        } catch (Exception e) {
            log.error("❌ [KAFKA ADMIN] Error al listar tópicos: {}", e.getMessage());
            throw new RuntimeException("Error al listar tópicos de Kafka: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene el detalle completo y configuraciones avanzadas de un tópico específico.
     */
    public TopicResponseDto obtenerDetalleTopico(String name) {
        try {
            TopicDescription desc = adminClient.describeTopics(Collections.singletonList(name))
                    .allTopicNames().get(10, TimeUnit.SECONDS).get(name);

            ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, name);
            Config config = adminClient.describeConfigs(Collections.singletonList(resource))
                    .all().get(10, TimeUnit.SECONDS).get(resource);

            Map<String, String> configsMap = new HashMap<>();
            config.entries().forEach(entry -> configsMap.put(entry.name(), entry.value()));

            int partitions = desc.partitions().size();
            int repFactor = partitions > 0 ? desc.partitions().get(0).replicas().size() : 1;

            return TopicResponseDto.builder()
                    .name(name)
                    .partitions(partitions)
                    .replicationFactor(repFactor)
                    .internal(desc.isInternal())
                    .configurations(configsMap)
                    .build();
        } catch (Exception e) {
            log.error("❌ [KAFKA ADMIN] Error al obtener detalle del tópico '{}': {}", name, e.getMessage());
            throw new RuntimeException("Tópico '" + name + "' no encontrado o inaccesible: " + e.getMessage(), e);
        }
    }

    /**
     * Modifica particiones o configuraciones de retención / cleanup de un tópico existente.
     */
    public String modificarTopico(String name, TopicUpdateRequestDto dto) {
        try {
            // 1. Incrementar particiones si se solicita
            if (dto.getPartitions() != null && dto.getPartitions() > 0) {
                Map<String, NewPartitions> newPartitions = new HashMap<>();
                newPartitions.put(name, NewPartitions.increaseTo(dto.getPartitions()));
                adminClient.createPartitions(newPartitions).all().get(10, TimeUnit.SECONDS);
                log.info("✅ [KAFKA ADMIN] Particiones de '{}' incrementadas a {}", name, dto.getPartitions());
            }

            // 2. Modificar configuraciones (retention.ms, cleanup.policy)
            Collection<AlterConfigOp> ops = new ArrayList<>();
            if (dto.getRetentionMs() != null) {
                ConfigEntry entry = new ConfigEntry(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(dto.getRetentionMs()));
                ops.add(new AlterConfigOp(entry, AlterConfigOp.OpType.SET));
            }
            if (dto.getCleanupPolicy() != null) {
                ConfigEntry entry = new ConfigEntry(TopicConfig.CLEANUP_POLICY_CONFIG, dto.getCleanupPolicy());
                ops.add(new AlterConfigOp(entry, AlterConfigOp.OpType.SET));
            }

            if (!ops.isEmpty()) {
                ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, name);
                Map<ConfigResource, Collection<AlterConfigOp>> updateMap = new HashMap<>();
                updateMap.put(resource, ops);
                adminClient.incrementalAlterConfigs(updateMap).all().get(10, TimeUnit.SECONDS);
                log.info("✅ [KAFKA ADMIN] Configuraciones de '{}' actualizadas exitosamente", name);
            }

            return "Tópico '" + name + "' modificado exitosamente.";
        } catch (Exception e) {
            log.error("❌ [KAFKA ADMIN] Error al modificar tópico '{}': {}", name, e.getMessage());
            throw new RuntimeException("Error al modificar tópico en Kafka: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un tópico del clúster.
     */
    public String eliminarTopico(String name) {
        try {
            adminClient.deleteTopics(Collections.singletonList(name)).all().get(10, TimeUnit.SECONDS);
            log.info("✅ [KAFKA ADMIN] Tópico '{}' eliminado del clúster", name);
            return "Tópico '" + name + "' eliminado exitosamente.";
        } catch (Exception e) {
            log.error("❌ [KAFKA ADMIN] Error al eliminar tópico '{}': {}", name, e.getMessage());
            throw new RuntimeException("Error al eliminar tópico de Kafka: " + e.getMessage(), e);
        }
    }

    /**
     * Lista e inspecciona los Consumer Groups registrados en el clúster.
     */
    public List<ConsumerGroupResponseDto> listarConsumerGroups() {
        try {
            Collection<ConsumerGroupListing> listings = adminClient.listConsumerGroups().all().get(10, TimeUnit.SECONDS);
            if (listings.isEmpty()) {
                return Collections.emptyList();
            }

            List<String> groupIds = listings.stream().map(ConsumerGroupListing::groupId).collect(Collectors.toList());
            Map<String, ConsumerGroupDescription> descriptions = adminClient.describeConsumerGroups(groupIds).all().get(10, TimeUnit.SECONDS);

            List<ConsumerGroupResponseDto> responseList = new ArrayList<>();
            for (Map.Entry<String, ConsumerGroupDescription> entry : descriptions.entrySet()) {
                String gId = entry.getKey();
                ConsumerGroupDescription desc = entry.getValue();

                List<String> members = desc.members().stream()
                        .map(m -> m.consumerId() + " (" + m.host() + ")")
                        .collect(Collectors.toList());

                responseList.add(ConsumerGroupResponseDto.builder()
                        .groupId(gId)
                        .state(desc.state() != null ? desc.state().name() : "UNKNOWN")
                        .simpleConsumerGroup(desc.isSimpleConsumerGroup())
                        .membersCount(desc.members().size())
                        .members(members)
                        .build());
            }

            return responseList;
        } catch (Exception e) {
            log.error("❌ [KAFKA ADMIN] Error al listar Consumer Groups: {}", e.getMessage());
            throw new RuntimeException("Error al listar Consumer Groups de Kafka: " + e.getMessage(), e);
        }
    }
}
