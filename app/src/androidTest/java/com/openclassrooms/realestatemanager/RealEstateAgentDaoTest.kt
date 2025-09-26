package com.openclassrooms.realestatemanager

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.realestatemanager.data.AppDataBase
import com.openclassrooms.realestatemanager.data.dao.RealEstateAgentDao
import com.openclassrooms.realestatemanager.data.model.RealEstateAgentDb
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class RealEstateAgentDaoTest {

    private lateinit var database: AppDataBase
    private lateinit var agentDao: RealEstateAgentDao

    @Before
    fun createDatabase() {
        database = Room
            .inMemoryDatabaseBuilder(AppApplication.appContext, AppDataBase::class.java)
            .build()

        agentDao = database.realEstateAgentDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun create_agent_return_id_generated_and_fetch_one_specific_agent() = runTest {
        //GIVEN
        val generatedId= agentDao.createAgent(RealEstateAgentDb(
            id = 0,
            name = "Josie"
        ))

        //WHEN
        //get the new agent created
        val agentFetched = agentDao.fetchOneAgent(generatedId)

        //THEN
        //id is generated and positive
        //TODO : assertK
        assertTrue(generatedId > 0)
        assertEquals("Josie", agentFetched.name )
    }


    @Test
    fun fetch_all_agents_return_all_agents()= runTest{
        //GIVEN
        agentDao.createAgent(RealEstateAgentDb(
            id = 0,
            name = "Josie"
        ))
        agentDao.createAgent(RealEstateAgentDb(
            id=0,
            name = "Marie"
        ))
        //WHEN
        val agents = agentDao.fetchAllAgents()
        //THEN
        //TODO: assertK + meme nom
        assertEquals(2, agents.size)

    }
}