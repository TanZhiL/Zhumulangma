package com.gykj.zhumulangma.home.adapter;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.bean.SearchSuggestItem;

import java.util.List;

/**
 * Author: Thomas.
 * Date: 2019/9/18 14:10
 * Email: 1071931588@qq.com
 * Description:
 */
public class SearchSuggestAdapter  extends BaseMultiItemQuickAdapter<SearchSuggestItem, BaseViewHolder> {

    public SearchSuggestAdapter(List<SearchSuggestItem> data) {
        super(data);
        addItemType(SearchSuggestItem.ALBUM, R.layout.home_item_search_suggest_query);
        addItemType(SearchSuggestItem.QUERY,R.layout.home_item_search_suggest_query);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchSuggestItem item) {
        switch (helper.getItemViewType()) {
            case SearchSuggestItem.ALBUM:

                helper.setText(R.id.tv_label,item.mAlbumResult.getAlbumTitle());

                break;
            case SearchSuggestItem.QUERY:
                String highlightKeyword = item.mQueryResult.getHighlightKeyword();
                int start = highlightKeyword.indexOf("<em>");
                int end = highlightKeyword.lastIndexOf("</em>");
                SpannableString spannableString = new SpannableString(highlightKeyword.replace("<em>","").replace("</em>",""));
                if(start!=-1&&end!=-1){
                    ForegroundColorSpan colorSpan = new ForegroundColorSpan(mContext.getResources().getColor(R.color.colorPrimary));
                    spannableString.setSpan(colorSpan, start,end-"</em>".length()+1,Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                }

                helper.setText(R.id.tv_label,spannableString);

                break;

        }
    }
}