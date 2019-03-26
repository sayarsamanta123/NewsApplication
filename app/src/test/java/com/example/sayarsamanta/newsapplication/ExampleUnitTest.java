package com.example.sayarsamanta.newsapplication;

import android.content.Context;

import com.example.sayarsamanta.newsapplication.adapter.NewsListAdapter;
import com.example.sayarsamanta.newsapplication.model.Post;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
//    @Test
//    public void addition_isCorrect() {
//        assertEquals(4, 2 + 2);
//    }
    @Mock
    Context context;

    private NewsListAdapter liveZoneGridAdapter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void addGridItemsToViewNotifiesParentAndAddsItemToTileList() {

        liveZoneGridAdapter = spy(new NewsListAdapter(context));

        doNothing().when(liveZoneGridAdapter).internalNotifyItemInserted(anyInt());

        liveZoneGridAdapter.addGridItemsToView(0, new Post("test", "test", "test", "test","test"));
        verify(liveZoneGridAdapter).internalNotifyItemInserted(0);
    }



}
