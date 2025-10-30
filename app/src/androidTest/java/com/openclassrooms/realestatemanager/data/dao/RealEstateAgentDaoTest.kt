package com.openclassrooms.realestatemanager.data.dao

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.extracting
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import com.openclassrooms.realestatemanager.AppApplication
import com.openclassrooms.realestatemanager.data.AppDataBase
import com.openclassrooms.realestatemanager.data.model.RealEstateAgentDb
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class RealEstateAgentDaoTest {

    private lateinit var database: AppDataBase
    private lateinit var agentDao: RealEstateAgentDao

    @Before
    fun createDatabase() {
        database = Room
            .inMemoryDatabaseBuilder(AppApplication.Companion.appContext, AppDataBase::class.java)
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
        val generatedId = agentDao.createAgent(
            RealEstateAgentDb(
                id = 0,
                name = "Josie"
            )
        )

        //WHEN
        //get the new agent created
        val agentFetched = agentDao.fetchOneAgent(generatedId)

        //THEN
        //id is generated and positive
        assertThat(generatedId > 0).isTrue()
        assertThat("Josie").isEqualTo(agentFetched.name)
    }


    @Test
    fun fetch_all_agents_return_all_agents()= runTest {
        //GIVEN
        agentDao.createAgent(
            RealEstateAgentDb(
                id = 0,
                name = "Josie"
            )
        )
        agentDao.createAgent(
            RealEstateAgentDb(
                id = 0,
                name = "Marie"
            )
        )
        //WHEN
        val agents = agentDao.fetchAllAgents()
        //THEN
        assertThat(agents.size).isEqualTo(2)
        assertThat(agents)
            .extracting(RealEstateAgentDb::name)
            .containsExactly("Josie", "Marie")
    }
}