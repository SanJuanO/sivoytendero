package com.sivoyrepartidor.acmarkettendero.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sivoyrepartidor.acmarkettendero.R;
import com.sivoyrepartidor.acmarkettendero.model.PedidoModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PedidosAdapter extends BaseAdapter {

    Context mContext;
    List<PedidoModel>listaPedidos;
    public PedidosAdapter(Context contex, List<PedidoModel>pedidos){
        this.mContext=contex;
        this.listaPedidos=pedidos;
    }

    public void setListaPedidos(List<PedidoModel>pedidos){
        this.listaPedidos=pedidos;
    }

    @Override
    public int getCount() {
        return listaPedidos.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        PedidoModel pedido=listaPedidos.get(i);

        if (view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(R.layout.item_pedido, null);
        }

        TextView tvEstatus=view.findViewById(R.id.tvEstatusPedidoItem);
        TextView tvIdPedido=view.findViewById(R.id.tvIdPedidoItem);
        TextView tvNombre=view.findViewById(R.id.tvNombrePedidoItem);
        TextView tvDireccion=view.findViewById(R.id.tvDireccionPedidoItem);
        TextView tvTotal=view.findViewById(R.id.tvTotalpedidoItem);
        TextView tvNombreRepartidor=view.findViewById(R.id.tvNombrePedidoPor);
        TextView tvHorarioTienda=view.findViewById(R.id.tvHorarioPedidoItem);
        TextView tvfechaPedido=view.findViewById(R.id.tvFechaPedidoItem);
        ImageView ivTienda=view.findViewById(R.id.ivTiendaPedidoItem);

        tvNombre.setText(pedido.TIENDA);
        tvEstatus.setText("Pedido "+pedido.ESTATUS);
        tvIdPedido.setText(""+pedido.PK);
        tvNombreRepartidor.setText(pedido.CLIENTE);
        tvfechaPedido.setText(pedido.FECHA_C);
        tvDireccion.setText(""+pedido.DIRECCION);
        tvHorarioTienda.setText(pedido.HORARIO);
        tvTotal.setText("Total: $ "+String.valueOf(pedido.TOTAL));

        if(!pedido.IMAGEN_TIENDA.isEmpty()){
            Picasso.with(mContext).load(pedido.IMAGEN_TIENDA).placeholder(R.drawable.iv_placeholder)
                    .error(R.drawable.iv_placeholder).into(ivTienda);
        }

        return view;
    }
}
