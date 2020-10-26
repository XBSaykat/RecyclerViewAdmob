package com.xb.saykat.recyclerviewadmob;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;

import java.util.List;

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int MENU_ITEM_VIEW_TYPE = 0;
    private static final int BANNER_AD_VIEW_TYPE = 1;
    private final Context context;
    private final List<Object> recyclerViewItems;
    public RecyclerViewAdapter(Context context, List<Object> recyclerViewItems) {
        this.context = context;
        this.recyclerViewItems = recyclerViewItems;
    }
    public class MenuItemViewHolder extends RecyclerView.ViewHolder {
        private TextView menuItemName;
        private TextView menuItemDescription;
        private TextView menuItemPrice;
        private TextView menuItemCategory;
        private ImageView menuItemImage;

        MenuItemViewHolder(View view) {
            super(view);
            menuItemImage = view.findViewById(R.id.menu_item_image);
            menuItemName = view.findViewById(R.id.menu_item_name);
            menuItemPrice = view.findViewById(R.id.menu_item_price);
            menuItemCategory = view.findViewById(R.id.menu_item_category);
            menuItemDescription = view.findViewById(R.id.menu_item_description);
        }
    }

    public class AdViewHolder extends RecyclerView.ViewHolder {

        AdViewHolder(View view) {
            super(view);
        }
    }

    @Override
    public int getItemCount() {
        return recyclerViewItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position % MainActivity.ITEMS_PER_AD == 0) ? BANNER_AD_VIEW_TYPE : MENU_ITEM_VIEW_TYPE;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case MENU_ITEM_VIEW_TYPE:
                View menuItemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.menu_item_container, viewGroup, false);
                return new MenuItemViewHolder(menuItemLayoutView);
            case BANNER_AD_VIEW_TYPE:
            default:
                View bannerLayoutView = LayoutInflater.from(
                        viewGroup.getContext()).inflate(R.layout.banner_ad_container,
                        viewGroup, false);
                return new AdViewHolder(bannerLayoutView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case MENU_ITEM_VIEW_TYPE:
                MenuItemViewHolder menuItemHolder = (MenuItemViewHolder) holder;
                MenuItem menuItem = (MenuItem) recyclerViewItems.get(position);

                String imageName = menuItem.getImageName();
                int imageResID = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());

                menuItemHolder.menuItemImage.setImageResource(imageResID);
                menuItemHolder.menuItemName.setText(menuItem.getName());
                menuItemHolder.menuItemPrice.setText(menuItem.getPrice());
                menuItemHolder.menuItemCategory.setText(menuItem.getCategory());
                menuItemHolder.menuItemDescription.setText(menuItem.getDescription());
                break;
            case BANNER_AD_VIEW_TYPE:
            default:
                AdViewHolder bannerHolder = (AdViewHolder) holder;
                AdView adView = (AdView) recyclerViewItems.get(position);
                ViewGroup adCardView = (ViewGroup) bannerHolder.itemView;

                if (adCardView.getChildCount() > 0) {
                    adCardView.removeAllViews();
                }
                if (adView.getParent() != null) {
                    ((ViewGroup) adView.getParent()).removeView(adView);
                }

                if(position!=0){
                    adCardView.addView(adView);
                }
        }
    }

}
