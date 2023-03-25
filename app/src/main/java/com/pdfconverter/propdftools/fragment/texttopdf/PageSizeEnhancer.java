package com.pdfconverter.propdftools.fragment.texttopdf;

import android.content.Context;
import androidx.annotation.NonNull;

import pdfconverterpro.R;
import com.pdfconverter.propdftools.interfaces.Enhancer;
import com.pdfconverter.propdftools.pdfModel.EnhancementOptionsEntity;
import com.pdfconverter.propdftools.pdfPreferences.TextToPdfPreferences;
import com.pdfconverter.propdftools.util.PageSizeUtils;

/**
 * An {@link Enhancer} that lets you select page size.
 */
public class PageSizeEnhancer implements Enhancer {

    private final PageSizeUtils mPageSizeUtils;
    private final EnhancementOptionsEntity mEnhancementOptionsEntity;

    PageSizeEnhancer(@NonNull final Context context) {
        mPageSizeUtils = new PageSizeUtils(context);
        mEnhancementOptionsEntity = new EnhancementOptionsEntity(
                context, R.drawable.ic_page_size_24dp, R.string.set_page_size_text);

        PageSizeUtils.mPageSize = new TextToPdfPreferences(context).getPageSize();
    }

    @Override
    public void enhance() {
        mPageSizeUtils.showPageSizeDialog(false);
    }

    @Override
    public EnhancementOptionsEntity getEnhancementOptionsEntity() {
        return mEnhancementOptionsEntity;
    }

}
