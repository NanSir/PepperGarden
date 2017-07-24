package com.space.app.chili;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.support.v7.widget.Toolbar;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.sir.app.base.BaseActivity;
import com.space.app.chili.common.BusEvent;
import com.space.app.chili.common.BusProvider;
import com.space.app.chili.upload.UploadService;
import com.space.app.chili.utils.NetWorkUtils;
import com.space.app.chili.utils.ToolAlert;
import com.space.app.chili.widget.AnimButtonLayout;
import com.space.app.chili.widget.AnimDownloadProgressButton;
import com.squareup.otto.Subscribe;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;



/**
 * 打印界面
 * Created by zhuyinan on 2017/6/5.
 */
public class PrintActivity extends BaseActivity {

    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.mAnimButtonLayout)
    AnimButtonLayout mAnimButtonLayout;
    PhotoAdapter adapter;

    final int UNIT_IN_INCH = 1000;

    @Override
    public int bindLayout() {
        return R.layout.activity_print;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<String> photos = getIntent().getStringArrayListExtra("photos");
        adapter = new PhotoAdapter(photos, getLayoutInflater());
        listView.setAdapter(adapter);
    }

    @Override
    public void doBusiness(Context mContext) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == adapter.getCount()) {
                    PhotoPicker.builder()
                            .setPhotoCount(3)
                            .setShowCamera(true)
                            .setPreviewEnabled(false)
                            .setSelected(adapter.cloneItems())
                            .start(PrintActivity.this);
                } else {
                    PhotoPreview.builder()
                            .setPhotos(adapter.cloneItems())
                            .setCurrentItem(position)
                            .start(PrintActivity.this);
                }
            }
        });

        mAnimButtonLayout.setCurrentText("上 传");
        mAnimButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetWorkUtils.isConnected(getContext())) {
                    mAnimButtonLayout.setState(AnimDownloadProgressButton.DOWNLOADING);
                    //上传
                    startUploadService();
                    mAnimButtonLayout.setEnabled(false);
                } else {
                    ToolAlert.showLong(getContext(), "网络异常");
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.print, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_print:
                try {
                    print();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Subscribe
    public void onProgress(BusEvent busEvent) {
        switch (busEvent.id) {
            case 100:
                mAnimButtonLayout.setProgressText("上传", busEvent.digit);
                break;
            case 101:
                mAnimButtonLayout.setCurrentText("上传完成");
                break;
            case 102:
                mAnimButtonLayout.setCurrentText("上传失败");
                break;
        }
    }

    //上传
    public void startUploadService() {
        mAnimButtonLayout.setState(AnimDownloadProgressButton.DOWNLOADING);
        ArrayList files = getIntent().getStringArrayListExtra("photos");
        Intent intent = new Intent(getContext(), UploadService.class);
        intent.putStringArrayListExtra("fileUrls", files);
        startService(intent);
    }


    //打印
    private void print() {
        // 打印服务,访问打印队列，并提供PrintDocumentAdapter类支持
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        // PrintDocumentAdapter – 提供自定义打印文档的基础类。
        printManager.print("Print photo", new PrintDocumentAdapter() {
            private int mRenderPageWidth;
            private int mRenderPageHeight;
            // PrintAttributes可以让你指定一种颜色模式，媒体尺寸，边距还有分辨率
            private PrintAttributes mPrintAttributes;
            // PrintDocumentInfo对象，用于描述所打印的内容
            private PrintDocumentInfo mDocumentInfo;
            private Context mPrintContext;

            // onLayout方法在PrintAttribute改变的时候就会调用，目的就是为了创建PrintDocumentInfo对象，描述所打印的内容
            @Override
            public void onLayout(final PrintAttributes oldAttributes,
                                 final PrintAttributes newAttributes,
                                 final CancellationSignal cancellationSignal,
                                 final LayoutResultCallback callback, final Bundle metadata) {

                if (cancellationSignal.isCanceled()) {
                    callback.onLayoutCancelled();
                    return;
                }

                boolean layoutNeeded = false;

                final int density = Math.max(newAttributes.getResolution().getHorizontalDpi(), newAttributes.getResolution().getVerticalDpi());

                final int marginLeft = (int) (density * (float) newAttributes.getMinMargins().getLeftMils() / UNIT_IN_INCH);
                final int marginRight = (int) (density * (float) newAttributes.getMinMargins().getRightMils() / UNIT_IN_INCH);
                final int contentWidth = (int) (density * (float) newAttributes.getMediaSize().getWidthMils() / UNIT_IN_INCH) - marginLeft - marginRight;
                if (mRenderPageWidth != contentWidth) {
                    mRenderPageWidth = contentWidth;
                    layoutNeeded = true;
                }

                final int marginTop = (int) (density * (float) newAttributes.getMinMargins().getTopMils() / UNIT_IN_INCH);
                final int marginBottom = (int) (density * (float) newAttributes.getMinMargins().getBottomMils() / UNIT_IN_INCH);
                final int contentHeight = (int) (density * (float) newAttributes.getMediaSize().getHeightMils() / UNIT_IN_INCH) - marginTop - marginBottom;
                if (mRenderPageHeight != contentHeight) {
                    mRenderPageHeight = contentHeight;
                    layoutNeeded = true;
                }

                if (mPrintContext == null
                        || mPrintContext.getResources().getConfiguration().densityDpi != density) {
                    Configuration configuration = new Configuration();
                    configuration.densityDpi = density;
                    mPrintContext = createConfigurationContext(configuration);
                    mPrintContext.setTheme(android.R.style.Theme_Holo_Light);
                }

                if (!layoutNeeded) {
                    callback.onLayoutFinished(mDocumentInfo, false);
                    return;
                }

                final List<String> items = adapter.cloneItems();
                /**
                 * 耗时异步任务
                 */
                new AsyncTask<Void, Void, PrintDocumentInfo>() {
                    @Override
                    protected void onPreExecute() {
                        cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
                            @Override
                            public void onCancel() {
                                cancel(true);
                            }
                        });
                        mPrintAttributes = newAttributes;
                    }

                    // 后台操作
                    @Override
                    protected PrintDocumentInfo doInBackground(Void... params) {
                        try {
                            PhotoAdapter adapter = new PhotoAdapter(items,
                                    (LayoutInflater) mPrintContext
                                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE));

                            int currentPage = 0;
                            int pageContentHeight = 0;
                            int viewType = -1;
                            View view = null;
                            LinearLayout dummyParent = new LinearLayout(
                                    mPrintContext);
                            dummyParent.setOrientation(LinearLayout.VERTICAL);

                            final int itemCount = adapter.getCount();
                            for (int i = 0; i < itemCount; i++) {
                                if (isCancelled()) {
                                    return null;
                                }

                                final int nextViewType = adapter.getItemViewType(i);
                                if (viewType == nextViewType) {
                                    view = adapter.getView(i, view, dummyParent);
                                } else {
                                    view = adapter.getView(i, null, dummyParent);
                                }
                                viewType = nextViewType;
                                measureView(view);
                                pageContentHeight += view.getMeasuredHeight();
                                if (pageContentHeight > mRenderPageHeight) {
                                    pageContentHeight = view.getMeasuredHeight();
                                    currentPage++;
                                }
                            }

                            PrintDocumentInfo info = new PrintDocumentInfo.Builder("PrintPhoto.pdf").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                                    .setPageCount(currentPage + 1).build();

                            callback.onLayoutFinished(info, true);
                            return info;
                        } catch (Exception e) {
                            callback.onLayoutFailed(null);
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    protected void onPostExecute(PrintDocumentInfo result) {
                        mDocumentInfo = result;
                    }

                    @Override
                    protected void onCancelled(PrintDocumentInfo result) {
                        callback.onLayoutCancelled();
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        (Void[]) null);
            }

            /**
             * 这个方法在需要使用文件描述符修改PDF文件的时候，就会调用，
             * 特别要注意的是这个方法（PrintDocumentAdapter的方法也一样）是在主线程调用的；
             * 在这里提出，因为此方法最好是在后台运行，因为考虑到我们会做一些文件IO操作。
             * 这个方法的主要目的就是往PDF里面写内容，把PDF内容写到文件里，然后调用对应的回调方法。
             */
            @Override
            public void onWrite(final PageRange[] pages,
                                final ParcelFileDescriptor destination,
                                final CancellationSignal cancellationSignal,
                                final WriteResultCallback callback) {

                if (cancellationSignal.isCanceled()) {
                    callback.onWriteCancelled();
                    return;
                }

                final List<String> items = adapter.cloneItems();

                new AsyncTask<Void, Void, Void>() {
                    private final SparseIntArray mWrittenPages = new SparseIntArray();
                    // PrintedPdfDocument:基于特定PrintAttributeshelper创建PDF。
                    private final PrintedPdfDocument mPdfDocument = new PrintedPdfDocument(
                            PrintActivity.this, mPrintAttributes);

                    @Override
                    protected void onPreExecute() {
                        cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
                            @Override
                            public void onCancel() {
                                cancel(true);
                            }
                        });
                    }

                    @Override
                    protected Void doInBackground(Void... params) {
                        PhotoAdapter adapter = new PhotoAdapter(items, (LayoutInflater) mPrintContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE));

                        int currentPage = -1;
                        int pageContentHeight = 0;
                        int viewType = -1;
                        View view = null;
                        PdfDocument.Page page = null;
                        LinearLayout dummyParent = new LinearLayout(
                                mPrintContext);
                        dummyParent.setOrientation(LinearLayout.VERTICAL);

                        final float scale = Math.min((float) mPdfDocument
                                .getPageContentRect().width()
                                / mRenderPageWidth, (float) mPdfDocument
                                .getPageContentRect().height()
                                / mRenderPageHeight);

                        final int itemCount = adapter.getCount();
                        for (int i = 0; i < itemCount; i++) {
                            if (isCancelled()) {
                                return null;
                            }

                            final int nextViewType = adapter.getItemViewType(i);
                            if (viewType == nextViewType) {
                                view = adapter.getView(i, view, dummyParent);
                            } else {
                                view = adapter.getView(i, null, dummyParent);
                            }
                            viewType = nextViewType;
                            measureView(view);

                            pageContentHeight += view.getMeasuredHeight();
                            if (currentPage < 0
                                    || pageContentHeight > mRenderPageHeight) {
                                pageContentHeight = view.getMeasuredHeight();
                                currentPage++;
                                if (page != null) {
                                    mPdfDocument.finishPage(page);
                                }
                                if (containsPage(pages, currentPage)) {
                                    page = mPdfDocument.startPage(currentPage);
                                    page.getCanvas().scale(scale, scale);
                                    mWrittenPages.append(mWrittenPages.size(),
                                            currentPage);
                                } else {
                                    page = null;
                                }
                            }

                            if (page != null) {
                                view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                                view.draw(page.getCanvas());
                                page.getCanvas().translate(0, view.getHeight());
                            }
                        }

                        if (page != null) {
                            mPdfDocument.finishPage(page);
                        }

                        try {
                            mPdfDocument.writeTo(new FileOutputStream(destination.getFileDescriptor()));
                            PageRange[] pageRanges = computeWrittenPageRanges(mWrittenPages);
                            callback.onWriteFinished(pageRanges);
                        } catch (IOException ioe) {
                            callback.onWriteFailed(null);
                        } finally {
                            mPdfDocument.close();
                        }
                        return null;
                    }

                    @Override
                    protected void onCancelled(Void result) {
                        callback.onWriteCancelled();
                        mPdfDocument.close();
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        (Void[]) null);
            }

            private void measureView(View view) {
                final int widthMeasureSpec = ViewGroup.getChildMeasureSpec(
                        MeasureSpec.makeMeasureSpec(mRenderPageWidth, MeasureSpec.EXACTLY), 0,
                        view.getLayoutParams().width);
                final int heightMeasureSpec = ViewGroup.getChildMeasureSpec(
                        MeasureSpec.makeMeasureSpec(mRenderPageHeight, MeasureSpec.EXACTLY), 0,
                        view.getLayoutParams().height);
                view.measure(widthMeasureSpec, heightMeasureSpec);
            }

            private PageRange[] computeWrittenPageRanges(SparseIntArray writtenPages) {
                List<PageRange> pageRanges = new ArrayList<>();
                int start = -1;
                int end;
                final int writtenPageCount = writtenPages.size();
                for (int i = 0; i < writtenPageCount; i++) {
                    if (start < 0) {
                        start = writtenPages.valueAt(i);
                    }
                    int oldEnd = end = start;
                    while (i < writtenPageCount && (end - oldEnd) <= 1) {
                        oldEnd = end;
                        end = writtenPages.valueAt(i);
                        i++;
                    }
                    PageRange pageRange = new PageRange(start, end);
                    pageRanges.add(pageRange);
                    start = -1;
                }
                PageRange[] pageRangesArray = new PageRange[pageRanges.size()];
                pageRanges.toArray(pageRangesArray);
                return pageRangesArray;
            }

            private boolean containsPage(PageRange[] pageRanges, int page) {
                final int pageRangeCount = pageRanges.length;
                for (int i = 0; i < pageRangeCount; i++) {
                    if (pageRanges[i].getStart() <= page && pageRanges[i].getEnd() >= page) {
                        return true;
                    }
                }
                return false;
            }
        }, null);
    }

    private class PhotoAdapter extends BaseAdapter {

        private final List<String> mItems;

        private final LayoutInflater mInflater;

        public PhotoAdapter(List<String> items, LayoutInflater inflater) {
            mItems = items;
            mInflater = inflater;
        }

        public ArrayList<String> cloneItems() {
            return new ArrayList<>(mItems);
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_output, parent, false);
            }
            ImageView photo = (ImageView) convertView.findViewById(R.id.iv_photo);
            photo.setImageURI(Uri.parse(mItems.get(position)));
            return convertView;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getBusInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getBusInstance().unregister(this);
    }

}
