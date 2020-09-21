package com.sivoyrepartidor.acmarkettendero.model;

import java.io.Serializable;
import java.util.List;

public class PedidoModel implements Serializable {

    public int PK;
    public String PK_CLIENTE;
    public String CLIENTE;
    public String TIENDA ;
    public String  DIRECCION_TIENDA;
    public String  LATITUD_TIENDA;
    public String  LONGITUD_TIENDA;
    public String  DIRECCION;
    public String  LATITUD;
    public String  LONGITUD;
    public String PK_REPARTIDOR;
    public String PK_ESTATUS;
    public String ESTATUS;
    public String TELEFONO_CLIENTE;
    public String  BORRADO;
    public double  PRECIO_ENTREGA;
    public double  TOTAL;
    public String  FECHA_C;
    public String  FECHA_M;
    public String  FECHA_D;
    public String  USUARIO_C;
    public String  USUARIO_M;
    public String  USUARIO_D;
    public String  PK_TIENDA;
    public String  PK_CATEGORIA;
    public String METODO_PAGO;
    public double  COMISION_TARJETA;
    public String  IMAGEN;
    public String  IMAGEN_TIENDA;
    public String  HORARIO;
    public String  REPARTIDOR;
    public List<PedidoDetalleModel> LISTA ;

}
