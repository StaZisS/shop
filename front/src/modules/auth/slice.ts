import {createSlice, PayloadAction} from "@reduxjs/toolkit";
import {RootState} from "@/store/store.tsx";

interface AuthState {
    token: string | null;
    isAuthenticated: boolean;
}

const initialState: AuthState = {
    token: localStorage.getItem("token"),
    isAuthenticated: Boolean(localStorage.getItem("token")),
};

const authSlice = createSlice({
    name: "auth",
    initialState,
    reducers: {
        setToken: (state, action: PayloadAction<string>) => {
            state.token = action.payload;
            state.isAuthenticated = true;
            localStorage.setItem("token", action.payload);
        },
        clearToken: (state) => {
            state.token = null;
            state.isAuthenticated = false;
            localStorage.removeItem("token");
        },
    },
});

export const selectIsAuthenticated = (state: RootState) =>
    state.auth.isAuthenticated;
export const {setToken, clearToken} = authSlice.actions;
export default authSlice.reducer;
