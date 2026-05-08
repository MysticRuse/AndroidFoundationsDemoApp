package com.sample.android.essentialcompose.networkingdatapersistence.retrofit

import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit interface for a blog API.
 * Demonstrates common HTTP methods and parameter handling.
 */
interface BlogApi {

    @GET("posts")
    suspend fun getPosts(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): List<BlogPost>

    @GET("posts/{id}")
    suspend fun getPostById(
        @Path("id") id: Int
    ): Response<BlogPost>

    @POST("posts")
    suspend fun createPost(
        @Body post: BlogPost
    ): BlogPost

    @PUT("posts/{id}")
    suspend fun updatePost(
        @Path("id") id: Int,
        @Body post: BlogPost
    ): Response<BlogPost>

    @DELETE("posts/{id}")
    suspend fun deletePost(
        @Path("id") id: Int
    ): Response<Unit>
}
