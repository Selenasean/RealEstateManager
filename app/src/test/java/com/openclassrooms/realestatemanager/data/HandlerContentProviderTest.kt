package com.openclassrooms.realestatemanager.data

import android.database.Cursor
import android.net.Uri
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull

import com.openclassrooms.realestatemanager.provider.HandlerContentProvider
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class HandlerContentProviderTest {

    private lateinit var realEstatesContentUri: Uri
    private lateinit var singleRealEstateUri :Uri
    private val realEstateId = 123L

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var mockRepository: Repository

    @MockK(relaxed = true)
    private lateinit var mockCursor: Cursor

    private lateinit var handler: HandlerContentProvider

    @Before
    fun setup() {
        //URIs
        realEstatesContentUri = HandlerContentProvider.CONTENT_URI
        singleRealEstateUri = Uri.withAppendedPath(HandlerContentProvider.CONTENT_URI, realEstateId.toString())

        mockCursor = mockk()
        every {
            mockRepository.getOneRealEstateWithCursor(any())
        } returns mockCursor
        every {
            mockRepository.getAllRealEstatesWithCursor()
        } returns mockCursor

        handler = HandlerContentProvider(mockRepository)
    }


    @Test
    fun query_for_all_realestates_should_call_correct_repository_method() {
        //WHEN
        val cursorResult = handler.query(
            realEstatesContentUri,
            null,
            null,
            null,
            null
        )
        //THEN
        assertThat(cursorResult).isNotNull()
        //verify repository is called
        verify(exactly = 1) { mockRepository.getAllRealEstatesWithCursor() }
        //verify handler return cursor
        assertThat(cursorResult).isEqualTo(mockCursor)
    }

    @Test
    fun query_for_one_realestate_should_call_correct_repository_method(){
        //WHEN
        val cursorResult = handler.query(
            singleRealEstateUri,
            null,
            null,
            null,
            null
        )

        ///THEN
        //verify repository method is called
        verify(exactly = 1) { mockRepository.getOneRealEstateWithCursor(realEstateId) }
        //check the cursor return
        assertThat(cursorResult).isEqualTo(mockCursor)

    }
}