package com.loquendodev.carhub.publisher;

import com.loquendodev.carhub.models.dto.UsuarioNotificacionMensajeDto;
import com.loquendodev.carhub.models.entity.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Productor desacoplado para emitir eventos de usuario (registro, sincronización OAuth, etc.)
 * hacia RabbitMQ de forma asíncrona.
 */
@Component
public class UsuarioEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(UsuarioEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${carhub.rabbitmq.exchange:carhub.exchange}")
    private String exchangeName;

    @Value("${carhub.rabbitmq.routingkey.usuario:backend-carhub.usuario.rk}")
    private String routingKey;

    @Autowired
    public UsuarioEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Publica un evento de notificación de usuario a RabbitMQ.
     */
    public void publicarEventoUsuario(Usuario usuario, String tipoEvento, String mensajeDetalle) {
        try {
            UsuarioNotificacionMensajeDto dto = new UsuarioNotificacionMensajeDto(
                    usuario.getIdUser(),
                    usuario.getNameUser(),
                    usuario.getEmailUser(),
                    usuario.getPhoneUser(),
                    tipoEvento,
                    mensajeDetalle,
                    new Date()
            );

            log.info("[PUBLISHER-USUARIO] Publicando evento '{}' para usuario '{}' en Exchange '{}' con RoutingKey '{}'",
                    tipoEvento, usuario.getEmailUser(), exchangeName, routingKey);

            rabbitTemplate.convertAndSend(exchangeName, routingKey, dto);
            log.info("[PUBLISHER-USUARIO] Evento '{}' publicado exitosamente a RabbitMQ para usuario ID #{}",
                    tipoEvento, usuario.getIdUser());

        } catch (Exception e) {
            log.error("[PUBLISHER-USUARIO] Error al publicar evento de usuario en RabbitMQ: {}", e.getMessage(), e);
        }
    }
}
