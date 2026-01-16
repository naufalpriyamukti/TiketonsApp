package com.tiketons.app.data.remote

import com.tiketons.app.utils.Constants
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = Constants.SUPABASE_URL,
        supabaseKey = Constants.SUPABASE_ANON_KEY
    ) {
        install(Auth) // Untuk Login/Register
        install(Postgrest) // Untuk Database CRUD
    }
}