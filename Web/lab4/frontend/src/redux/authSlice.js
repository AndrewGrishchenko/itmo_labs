import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import axios from "axios";

const API_URL = "http://localhost:8080/auth"

export const login = createAsyncThunk('auth/login', async (credentials) => {
    const response = await axios.post(`${API_URL}/login`, credentials);
    return response.data;
});

export const register = createAsyncThunk('auth/register', async (userData) => {
    const response = await axios.post(`${API_URL}/register`, userData);
    return response.data;
});

export const refreshAccessToken = createAsyncThunk('auth/refreshToken', async (refreshToken) => {
    const response = await axios.post(`${API_URL}/refreshToken`, { refreshToken });
    return response.data;
});

const initialState = {
    username: null,
    accessToken: localStorage.getItem("accessToken") || null,
    refreshToken: localStorage.getItem("refreshToken") || null,
    status: 'idle',
    error: null
};

const authsSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        setAccessToken: (state, action) => {
            state.accessToken = action.payload;
            localStorage.setItem("accessToken", action.payload);
        },
        setRefreshToken: (state, action) => {
            state.refreshToken = action.payload;
            localStorage.setItem("refreshToken", action.payload);
        },
        logout: (state) => {
            state.username = null;
            state.accessToken = null;
            state.refreshToken = null;
            localStorage.removeItem("accessToken");
            localStorage.removeItem("refreshToken");
        }
    },
    extraReducers: (builder) => {
        builder
            .addCase(login.pending, (state) => {
                state.status = 'loading';
                state.error = null;
            })
            .addCase(login.fulfilled, (state, action) => {
                if (action.payload.status === "Ok") {
                    state.accessToken = action.payload.accessToken;
                    localStorage.setItem("accessToken", action.payload.accessToken);

                    state.refreshToken = action.payload.refreshToken;
                    localStorage.setItem("refreshToken", action.payload.refreshToken);
                    state.username = action.payload.username;
                    state.status = "succeeded";
                } else {
                    state.status = "failed";
                    state.error = action.payload.message;
                }
            })
            .addCase(login.rejected, (state, action) => {
                state.status = 'failed';
                state.error = action.error.message;
            });
    }
});

export const { setAccessToken, setRefreshToken, logout } = authsSlice.actions;

export default authsSlice.reducer;