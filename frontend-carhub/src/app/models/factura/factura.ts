export interface Factura {
  nro_factura?: number;
  subtotal_factura: number;
  metodo_pago_factura: string;
  fecha_emision_factura: string;
  idUser: number;
  nameUser: string;
  emailUser: string;
  phoneUser: string;
  marcaVehiculo: string;
  modeloVehiculo: string;
  anioVehiculo: string;
  tipoBencinaVehiculo: string;
  tipoTransmisionVehiculo: string;
  tipoPropulsionVehiculo: string;
  nombreAutomotriz: string;
  direccionAutomotriz: string;
}
