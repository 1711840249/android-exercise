package com.rainsong.tiantiannews.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rainsong.tiantiannews.R;
import com.rainsong.tiantiannews.entity.NewsListEntity.Result.NewsEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxliaops on 17-1-13.
 */

public class NewsAdapter extends BaseAdapter {

    private static final int TYPE_NORMAL = 0;

    protected Context mContext;
    protected LayoutInflater mInflater;
    protected List<NewsEntity> mDataList;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;

    public NewsAdapter(Context context, List<NewsEntity> list) {
        mContext = context;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDataList = list == null ? new ArrayList<NewsEntity>()
                : new ArrayList<NewsEntity>(list);
        mImageLoader = ImageLoader.getInstance();
        mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.image_small_default) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.image_small_default) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.image_small_default) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 构建完成
    }

    public void updateData(List<NewsEntity> list) {
        this.mDataList = list == null ? new ArrayList<NewsEntity>()
                : new ArrayList<NewsEntity>(list);
        this.notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_NORMAL;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        if (position >= mDataList.size())
            return null;
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        NewsEntity item = mDataList.get(position);
        ViewHolder holder = null;
        switch (type) {
            case TYPE_NORMAL:
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.list_item, parent,
                            false);
                    holder = new ViewHolder(convertView);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                ImageView newsImageView = (ImageView) holder
                        .getView(R.id.list_item_image);
                TextView newsTitleView = (TextView) holder
                        .getView(R.id.list_item_title);
                newsTitleView.setText(item.title);
                mImageLoader.displayImage(item.thumbnail_pic_s, newsImageView,
                        mOptions);
                return convertView;
        }
        return null;
    }

    public class ViewHolder {
        private SparseArray<View> views = new SparseArray<View>();
        private View convertView;

        public ViewHolder(View convertView) {
            this.convertView = convertView;
        }

        public <T extends View> T getView(int resId) {
            View v = views.get(resId);
            if (null == v) {
                v = convertView.findViewById(resId);
                views.put(resId, v);
            }
            return (T) v;
        }
    }
}
