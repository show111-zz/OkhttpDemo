package com.example.huilee.okhttpdemo;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by huilee on 2017/4/9.
 */

public class CountingRequestBody extends RequestBody{

    protected RequestBody delegate;
    private Listener listener;
    private CountingSink countingSink;

    public CountingRequestBody(RequestBody delegate,Listener listener){
        this.delegate = delegate;
        this.listener = listener;
    }

    @Override
    public long contentLength()  {
        try {
            return delegate.contentLength();
        } catch (IOException e) {
            return -1;
        }
    }

    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        countingSink = new CountingSink(sink);
        BufferedSink bufferedSink = Okio.buffer(countingSink);
        delegate.writeTo(bufferedSink);
        bufferedSink.flush();
    }


    protected final class CountingSink extends ForwardingSink{
        private long bytesWritten;

        public CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            bytesWritten += byteCount;
            listener.onRequestProgress(bytesWritten,contentLength());
        }
    }

    public static interface Listener{
        // byteWrited：已写的字节数    contentLength：总共的字节数
        void onRequestProgress(long byteWrited,long contentLength);
    }


}
