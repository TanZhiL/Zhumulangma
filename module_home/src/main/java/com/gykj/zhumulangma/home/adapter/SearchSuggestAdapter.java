package com.gykj.zhumulangma.home.adapter;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.bean.SearchSuggestItem;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Thomas.
 * Date: 2019/9/18 14:10
 * Email: 1071931588@qq.com
 * Description:
 */
public class SearchSuggestAdapter extends BaseMultiItemQuickAdapter<SearchSuggestItem, BaseViewHolder> {

    public SearchSuggestAdapter(List<SearchSuggestItem> data) {
        super(data);
        addItemType(SearchSuggestItem.ALBUM, R.layout.home_item_search_suggest_album);
        addItemType(SearchSuggestItem.QUERY, R.layout.home_item_search_suggest_query);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchSuggestItem item) {
        String highlightKeyword="";
        switch (helper.getItemViewType()) {
            case SearchSuggestItem.ALBUM:
                highlightKeyword=item.mAlbumResult.getHightlightAlbumTitle();
                helper.setText(R.id.tv_label, item.mAlbumResult.getAlbumTitle());
                Glide.with(mContext).load(item.mAlbumResult.getCoverUrlSmall()).into((ImageView) helper.getView(R.id.iv_cover));
                helper.setText(R.id.tv_category,item.mAlbumResult.getCategoryName());
                helper.addOnClickListener(R.id.ll_play);
                break;
            case SearchSuggestItem.QUERY:
                highlightKeyword=item.mQueryResult.getHighlightKeyword();
                break;

        }

        SpannableStringBuilder spannableString = new SpannableStringBuilder(highlightKeyword);
        Pattern pattern = Pattern.compile("<em>(.+?)</em>");
        Matcher matcher = pattern.matcher(highlightKeyword);
        int count=0;
        while (matcher.find()){
            count++;
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(mContext.getResources().getColor(R.color.colorPrimary));
            spannableString.setSpan(colorSpan, matcher.start(), matcher.end(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
        for (int i = 0; i <count; i++) {
            int k = spannableString.toString().indexOf("<em>");
            spannableString.replace(k,k+4,"");
            int j = spannableString.toString().indexOf("</em>");
            spannableString.replace(j,j+5,"");
        }
        helper.setText(R.id.tv_label, spannableString);
    }

}