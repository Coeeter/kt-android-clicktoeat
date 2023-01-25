package com.nasportfolio.domain.validation

import com.nasportfolio.domain.validation.usecases.ValidateEmail
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Before
import org.junit.Test

class ValidateEmailTest {

    private lateinit var validateEmail: ValidateEmail

    @Before
    fun setUp() {
        validateEmail = ValidateEmail()
    }

    @Test
    fun `When email given is empty, should return field is required`() {
        val email = ""
        val error = validateEmail(email)
        assertNotNull(error)
        val message = error!!.error
        assertEquals("Email required!", message)
    }

    @Test
    fun `When email given is invalid, should return invalid field`() {
        val email = ";alksdjfalksdjfalskdfa"
        val error = validateEmail(email)
        assertNotNull(error)
        val message = error!!.error
        assertEquals("Invalid email provided", message)
    }

    @Test
    fun `When email given is valid, should return null`() {
        val email = "nasrullah01n@gmail.com"
        val error = validateEmail(email)
        assertEquals(error, null)
    }

    @Test
    fun `When field is updated, it should return field error with updated field`() {
        val email = ""
        val error = validateEmail(email, field = "credential")
        assertNotNull(error)
        val message = error!!.field
        assertEquals("credential", message)
    }
}