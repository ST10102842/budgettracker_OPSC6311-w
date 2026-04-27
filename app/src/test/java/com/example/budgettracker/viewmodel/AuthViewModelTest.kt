package com.example.budgettracker.viewmodel

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test

class AuthViewModelTest {

    private lateinit var authViewModel: AuthViewModel

    @Before
    fun setUp() {
        authViewModel = AuthViewModel()
    }

    @Test
    fun testInitialState() {
        val authState = authViewModel.authState.value
        assertFalse(authState.isLoggedIn)
        assertNull(authState.currentUser)
        assertNull(authState.errorMessage)
    }

    @Test
    fun testLoginWithValidCredentials() {
        val result = authViewModel.login("admin", "1234")

        assertTrue(result)
        assertTrue(authViewModel.authState.value.isLoggedIn)
        assertNotNull(authViewModel.authState.value.currentUser)
        assertEquals("admin", authViewModel.authState.value.currentUser?.username)
        assertNull(authViewModel.authState.value.errorMessage)
    }

    @Test
    fun testLoginWithInvalidPassword() {
        val result = authViewModel.login("admin", "wrongpassword")

        assertFalse(result)
        assertFalse(authViewModel.authState.value.isLoggedIn)
        assertNotNull(authViewModel.authState.value.errorMessage)
        assertTrue(authViewModel.authState.value.errorMessage!!.contains("password"))
    }

    @Test
    fun testLoginWithNonExistentUser() {
        val result = authViewModel.login("nonexistent", "1234")

        assertFalse(result)
        assertFalse(authViewModel.authState.value.isLoggedIn)
        assertNotNull(authViewModel.authState.value.errorMessage)
        assertTrue(authViewModel.authState.value.errorMessage!!.contains("not found"))
    }

    @Test
    fun testLoginWithEmptyCredentials() {
        val result = authViewModel.login("", "")

        assertFalse(result)
        assertFalse(authViewModel.authState.value.isLoggedIn)
        assertNotNull(authViewModel.authState.value.errorMessage)
    }

    @Test
    fun testRegisterWithValidData() {
        val result = authViewModel.register("newuser", "newuser@example.com", "password123")

        assertTrue(result)
        assertTrue(authViewModel.authState.value.isLoggedIn)
        assertNotNull(authViewModel.authState.value.currentUser)
        assertEquals("newuser", authViewModel.authState.value.currentUser?.username)
        assertEquals("newuser@example.com", authViewModel.authState.value.currentUser?.email)
    }

    @Test
    fun testRegisterWithDuplicateUsername() {
        // First registration
        authViewModel.register("testuser", "test@example.com", "password")
        authViewModel.logout()

        // Try to register again with same username
        val result = authViewModel.register("testuser", "another@example.com", "password")

        assertFalse(result)
        assertFalse(authViewModel.authState.value.isLoggedIn)
        assertNotNull(authViewModel.authState.value.errorMessage)
        assertTrue(authViewModel.authState.value.errorMessage!!.contains("already exists"))
    }

    @Test
    fun testRegisterWithEmptyFields() {
        val result = authViewModel.register("", "email@example.com", "password")

        assertFalse(result)
        assertFalse(authViewModel.authState.value.isLoggedIn)
        assertNotNull(authViewModel.authState.value.errorMessage)
    }

    @Test
    fun testLogout() {
        // First login
        authViewModel.login("admin", "1234")
        assertTrue(authViewModel.authState.value.isLoggedIn)

        // Then logout
        authViewModel.logout()

        assertFalse(authViewModel.authState.value.isLoggedIn)
        assertNull(authViewModel.authState.value.currentUser)
        assertNull(authViewModel.authState.value.errorMessage)
    }

    @Test
    fun testClearError() {
        authViewModel.login("admin", "wrongpassword")
        assertNotNull(authViewModel.authState.value.errorMessage)

        authViewModel.clearError()

        assertNull(authViewModel.authState.value.errorMessage)
    }
}

