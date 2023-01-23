package com.nasportfolio.domain.validation.usecases

import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class ValidateConfirmPasswordTest {

    lateinit var validateConfirmPassword: ValidateConfirmPassword

    @Before
    fun setUp() {
        validateConfirmPassword = ValidateConfirmPassword()
    }

    @Test
    fun `When password and confirm password is not equal, should return error`() {
        val error = validateConfirmPassword("password", "wrong password")
        assertNotNull(error)
        assertEquals("confirmPassword", error!!.field)
        assertEquals("Passwords do not match!", error.error)
    }

    @Test
    fun `When password and confirm password is equal, should return null`() {
        val error = validateConfirmPassword("password", "password")
        assertNull(error)
    }

    @Test
    fun `When password and confirm password is not equal and field is updated, should return error with updated field`() {
        val error = validateConfirmPassword("password", "wrong password", "password")
        assertNotNull(error)
        assertEquals("password", error!!.field)
        assertEquals("Passwords do not match!", error.error)
    }
}