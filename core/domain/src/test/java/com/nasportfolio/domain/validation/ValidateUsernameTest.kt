package com.nasportfolio.domain.validation

import com.nasportfolio.domain.validation.usecases.ValidateUsername
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Before
import org.junit.Test

class ValidateUsernameTest {

    private lateinit var validateUsername: ValidateUsername

    @Before
    fun setUp() {
        validateUsername = ValidateUsername()
    }

    @Test
    fun `When empty should return error`() {
        val username = ""
        val error = validateUsername(username)
        assertNotNull(error)
        assertEquals("username", error!!.field)
        assertEquals("Username required", error.error)
    }

    @Test
    fun `When valid username should return null`() {
        val username = "Coeeter"
        val error = validateUsername(username)
        assertEquals(null, error)
    }

    @Test
    fun `When username invalid and field is different, should return field error with updated field`() {
        val username = ""
        val error = validateUsername(username, field = "name")
        assertNotNull(error)
        assertEquals("name", error!!.field)
        assertEquals("Username required", error.error)
    }
}