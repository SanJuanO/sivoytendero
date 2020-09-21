package com.sivoyrepartidor.acmarkettendero.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sivoyrepartidor.acmarkettendero.R;
import com.sivoyrepartidor.acmarkettendero.model.ProductosModel;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

public class ProductosCarritoAdapter extends BaseAdapter {

    Context mContext;
    List<ProductosModel>listaProductos;

    public ProductosCarritoAdapter(Context context1, List<ProductosModel>lista){
        this.mContext=context1;
        this.listaProductos=lista;
    }

    public void setListaProductos(List<ProductosModel>lista){
        this.listaProductos=lista;
    }


    @Override
    public int getCount() {
        return listaProductos.size();
    }

    @Override
    public Object getItem(int i) {
        return listaProductos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ProductosModel producto=listaProductos.get(i);

        if (view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(R.layout.item_productos_carrito, null);
        }

        ImageButton btnElimina=view.findViewById(R.id.ivBoteProductoItem);
        TextView tvNombre=view.findViewById(R.id.tvNombreProductoItemCarrito);
        TextView tvCantidad=view.findViewById(R.id.tvCantidadItemCarrito);
        TextView tvPrecio=view.findViewById(R.id.tvPrecioItemCarrito);
        ImageView ivProducto=view.findViewById(R.id.ivProductoImagenItem);

        tvNombre.setText(producto.PRODUCTO);
        tvCantidad.setText(""+producto.CANTIDAD);
        DecimalFormat df = new DecimalFormat("#0.00");
        tvPrecio.setText("$ "+df.format(producto.PRECIO));
        if(!producto.IMAGEN.isEmpty() ){
            Picasso.with(mContext).load(producto.IMAGEN).placeholder(R.drawable.iv_placeholder)
                    .error(R.drawable.iv_placeholder).into(ivProducto);
        }

        /*
        btnElimina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AppDatabase db = Room.databaseBuilder(mContext,
                        AppDatabase.class, "polar-base").allowMainThreadQueries().build();
                db.PedidoDetalleDao().deleteProductosByPk(producto.PK);
                listaProductos.remove(producto);
                notifyDataSetChanged();
            }
        });
*/
        return view;
    }
}
