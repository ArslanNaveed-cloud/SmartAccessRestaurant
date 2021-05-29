package aust.fyp.pk.project.application.smartaccessrestaurant;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.carteasy.v1.lib.Carteasy;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class FoodItemAdaphter extends RecyclerView.Adapter<FoodItemAdaphter.viewholder> {

    private ArrayList<FoodDataModel> foodDataModels;
    private static Context context;
    private int numberofitems= 0;
    private FoodItemsList itemsList;
    private ArrayList<OrderDataModel> arrayList;
    OrderDataModel cartDataModels;
    long bill=0;
    private static Carteasy cs;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public FoodItemAdaphter(FoodItemsList context,ArrayList<FoodDataModel> foodDataModels) {
       this.context = context;
       cs = new Carteasy();
       cs.persistData(context, false);
        this.foodDataModels = foodDataModels;
        this.itemsList = context;
        arrayList = new ArrayList<>();
        sharedPreferences= itemsList.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.fooditemlistlayout,parent,false);

        return new FoodItemAdaphter.viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewholder holder, final int position) {
        String imagename = foodDataModels.get(position).getCoveriamge();
        String url;
        url  = Urls.DOMAIN+"/assets/foodimages/"+imagename;
        url = url.replace(" ","%20");
        Glide.with(context).load(url).
                into(holder.coverimage);
        holder.description.setText(foodDataModels.get(position).getDescription());
        holder.title.setText(foodDataModels.get(position).getTitle());
        holder.price.setText("Price: "+foodDataModels.get(position).getPrice());
         editor = sharedPreferences.edit();


        if(cs.doesIDExistInCart(foodDataModels.get(position).getTitle(),context)){
            long  quantity = cs.getLong(foodDataModels.get(position).getTitle(),  "Quantity",context);
            long bill = cs.getLong(foodDataModels.get(position).getTitle(),  "Bill",context);
            editor.putLong("bill", bill);
            holder.ItemCount.setText(""+quantity);
            itemsList.mycart.setVisibility(View.VISIBLE);
            itemsList.btn.setText("Proceed To Order Rs("+bill+")");
        }
        holder.increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(cs.doesIDExistInCart(foodDataModels.get(position).getTitle(),context)){

                    Log.d(TAG, "onClick: Id Found"+cs.get(foodDataModels.get(position).getTitle(),  "Quantity",context));
                    long  quantity = cs.getLong(foodDataModels.get(position).getTitle(),  "Quantity",context);
                    quantity = quantity+1;
                    bill+=Integer.parseInt(foodDataModels.get(position).getPrice());
                    editor.putLong("bill", bill);
                    cs.update(""+foodDataModels.get(position).getTitle(),"Quantity",quantity,context);
                    cs.update(""+foodDataModels.get(position).getTitle(), "Bill",  bill,context);

                    holder.ItemCount.setText(""+quantity);
                   itemsList.mycart.setVisibility(View.VISIBLE);
                    itemsList.btn.setText("Proceed To Order Rs("+bill+")");
                }else{
                    numberofitems = 1;
                    bill=bill+Integer.parseInt(foodDataModels.get(position).getPrice());
                    editor.putLong("bill", bill);
                    cs.add(""+foodDataModels.get(position).getTitle(), "name",  foodDataModels.get(position).getTitle());
                    cs.add(""+foodDataModels.get(position).getTitle(), "price",  foodDataModels.get(position).getPrice());
                    cs.add(""+foodDataModels.get(position).getTitle(), "Quantity", numberofitems);
                    cs.add(""+foodDataModels.get(position).getTitle(), "coverimage",  foodDataModels.get(position).getCoveriamge());
                    cs.add(""+foodDataModels.get(position).getTitle(), "Bill",  bill);

                    cs.commit(context);
                    holder.ItemCount.setText(""+numberofitems);
                    itemsList.mycart.setVisibility(View.VISIBLE);
                     itemsList.btn.setText("Proceed To Order Rs("+bill+")");

                }
            }
        });
        holder.decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cs.ViewAll(context).isEmpty() ||cs.ViewAll(context).equals("") ){
                    itemsList.mycart.setVisibility(View.GONE);
                }
                if(cs.doesIDExistInCart(foodDataModels.get(position).getTitle(),context)) {

                    long  quantity = cs.getLong(foodDataModels.get(position).getTitle(),  "Quantity",context);

                    if(quantity<=0){
                        cs.RemoveId(foodDataModels.get(position).getTitle(),context);
//                        itemsList.mycart.setVisibility(View.GONE);
                    }else{
                        quantity = quantity-1;
                        bill=bill-Integer.parseInt(foodDataModels.get(position).getPrice());

                        if(bill<=0 && quantity<=0){
                            cs.RemoveId(foodDataModels.get(position).getTitle(),context);
                            cs.update(""+foodDataModels.get(position).getTitle(),"Quantity",quantity,context);
                            cs.update(""+foodDataModels.get(position).getTitle(), "Bill",  bill,context);
                            itemsList.mycart.setVisibility(View.GONE);
                            holder.ItemCount.setText(""+quantity);
                        }else {
                            cs.update(""+foodDataModels.get(position).getTitle(),"Quantity",quantity,context);
                            cs.update(""+foodDataModels.get(position).getTitle(), "Bill",  bill,context);
                            itemsList.btn.setText("Proceed To Order Rs("+bill+")");
                            holder.ItemCount.setText(""+quantity);
                            itemsList.mycart.setVisibility(View.VISIBLE);
                        }


                    }

                }else{
                    if(bill<=0){
                        itemsList.mycart.setVisibility(View.GONE);

                    }
                    Toast.makeText(context, "You cannot remove this item from cart", Toast.LENGTH_SHORT).show();

                }
                }
             
        });

    }

    @Override
    public int getItemCount() {
        return foodDataModels.size();
    }

    public class viewholder extends RecyclerView.ViewHolder{

        private ImageView coverimage;
        private TextView title,description,price,ItemCount;
        private Button increment,decrement;

        public viewholder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            coverimage = itemView.findViewById(R.id.coverimage);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            price = itemView.findViewById(R.id.price);
            increment = itemView.findViewById(R.id.increment);
            decrement = itemView.findViewById(R.id.decrement);
            ItemCount = itemView.findViewById(R.id.itemcount);

        }
    }
}
