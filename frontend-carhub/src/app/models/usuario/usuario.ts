export type RolUsuario = 'USUARIO' | 'SUBADMIN' | 'SUPERADMIN';

export interface Usuario{
  idUser?: number;
  nameUser: string;
  emailUser: string;
  passwordUser: string;
  phoneUser: string;
  rolUsuario?: RolUsuario;
}

//estructura de modelo, en realidad es solo una interfaz (pero es así como se definen los modelos aqui xdd )