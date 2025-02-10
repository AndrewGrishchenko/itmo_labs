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

const initialState = {
    username: null,
    token: localStorage.getItem("token") || null,
    status: 'idle',
    error: null
};

const authsSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        setToken: (state, action) => {
            state.token = action.payload;
            localStorage.setItem("token", action.payload);
        },
        logout: (state) => {
            state.username = null;
            state.token = null;
            localStorage.removeItem("token");
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
                    state.token = action.payload.token;
                    state.username = action.payload.username;
                    localStorage.setItem("token", action.payload.token);
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

export const { setToken, logout } = authsSlice.actions;

export default authsSlice.reducer;