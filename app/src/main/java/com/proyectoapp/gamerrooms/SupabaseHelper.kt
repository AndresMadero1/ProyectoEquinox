package com.proyectoapp.gamerrooms

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseHelper {
    val client = createSupabaseClient(
        supabaseUrl = "https://aboyxrkcbqvhwcmeqfpz.supabase.co",
        supabaseKey = "sb_publishable_-MTyPcNhs5QRge3kjqACbg_0l9wixm3"
    ) {
        install(Postgrest)
    }
}
