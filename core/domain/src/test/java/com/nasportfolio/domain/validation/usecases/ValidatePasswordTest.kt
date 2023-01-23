package com.nasportfolio.domain.validation.usecases

import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class ValidatePasswordTest {

    lateinit var validatePassword: ValidatePassword

    @Before
    fun setUp() {
        validatePassword = ValidatePassword()
    }

    @Test
    fun `When in login mode and given empty password, should return error saying password is required`() {
        val password = ""
        val error = validatePassword(password, flag = ValidatePassword.LOGIN_FLAG)
        assertNotNull(error)
        assertEquals("password", error!!.field)
        assertEquals(ValidatePassword.Error.MissingEmail.message, error.error)
    }

    @Test
    fun `When in login mode and given not strong password, should return null`() {
        val password = "password"
        val error = validatePassword(password, flag = ValidatePassword.LOGIN_FLAG)
        assertEquals(null, error)
    }

    @Test
    fun `When in create mode and given empty password, should return error saying password rules`() {
        val password = ""
        val error = validatePassword(password, flag = ValidatePassword.CREATE_FLAG)
        assertNotNull(error)
        assertEquals("password", error!!.field)
        assertEquals(ValidatePassword.Error.InvalidEmail.message, error.error)
    }

    @Test
    fun `When in create mode and given invalid password, should return error saying password rules`() {
        val password = "password"
        val error = validatePassword(password, flag = ValidatePassword.CREATE_FLAG)
        assertNotNull(error)
        assertEquals("password", error!!.field)
        assertEquals(ValidatePassword.Error.InvalidEmail.message, error.error)
    }

    @Test
    fun `When in create mode and given valid password, should return null`() {
        val password = "StrongPassword!1"
        val error = validatePassword(password, flag = ValidatePassword.CREATE_FLAG)
        assertEquals(null, error)
    }

    @Test
    fun `When flag is left to default, mode should be create and error should return password rules`() {
        val password = "password"
        val error = validatePassword(password)
        assertNotNull(error)
        assertEquals("password", error!!.field)
        assertEquals(ValidatePassword.Error.InvalidEmail.message, error.error)
    }

    @Test
    fun `When flag given is invalid, should throw illegal argument exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            validatePassword(value = "", flag = "")
        }
    }
}