// src/store/user.js
import {defineStore} from 'pinia'
import {getCurrentUser as apiGetCurrentUser,
    login as apiLogin,
    register as apiRegister,
} from '../api/register'

export const useUserStore = defineStore('user', {
    state: () => ({
        isLoggedIn: false,
        userInfo: {
            id: null,
            email: '',
            username: '',
            verified: false,
            gender: '',
            role: ''
        },
        token: null, // 新增：JWT Token
    }),

    actions: {
        /**
         * 1. 登录：调用后端 /auth/login 接口，登录成功后拉取当前用户信息
         * @param {Object} payload { email: string, password: string }
         */
        async login(payload) {
            try {
                const res = await apiLogin({
                    email: payload.email,
                    password: payload.password,
                })
                // 保存 token
                this.token = res.data.token
                localStorage.setItem('jwtToken', this.token)

                // 拉取用户信息
                await this.fetchCurrentUser()
                this.isLoggedIn = true
                return true
            } catch (err) {
                this.resetState()
                return false
            }
        },

        /**
         * 1.5. 注册
         */
        async register(payload) {
            try {
                // 调用后端 /auth/login（HttpSession + Cookie）
                await apiRegister({
                    email: payload.email,
                    username: payload.username,
                    password: payload.password,
                    gender: payload.gender,
                    role: payload.role,
                })

                // 注册成功后，从后端 /user/userdata 获取用户信息
                await this.fetchCurrentUser()
                console.log("register success")
                return true
            } catch (err) {
                // 捕获后端返回的错误信息
                this.resetState()
                return false
            }
        },

        /**
         * 2. 退出登录：调用后端 /auth/logout，然后清空本地状态
         */
        async logout() {
            this.resetState()
            localStorage.removeItem('jwtToken')
        },

        /**
         * 3. 刷新/初始化时调用：尝试拉取当前用户信息，若成功则标记已登录，否则重置状态
         */
        async fetchCurrentUser() {
            if (!this.token) return this.resetState()
            try {
                const res = await apiGetCurrentUser(this.token) // 注意：apiGetCurrentUser 需在请求头携带 token
                this.userInfo = res.data
                this.isLoggedIn = true
            } catch (err) {
                this.resetState()
            }
        },


        resetState() {
            this.isLoggedIn = false
            this.token = null
            this.userInfo = {
                id: null,
                email: '',
                username: '',
                verified: false,
                gender: '',
                role: ''
            }
        },

        async initializeFromStorage() {
            const token = localStorage.getItem('jwtToken')
            if (token) {
                this.token = token
                await this.fetchCurrentUser()
            }
        }
    },

    getters: {
        // 获取当前登录状态
        loggedIn: (state) => state.isLoggedIn,

        // 获取当前用户信息
        currentUser: (state) => state.userInfo,

        // 获取用户名（假设userInfo中有username字段）
        username: (state) => state.userInfo?.username || '',

        // 获取用户邮箱（假设userInfo中有email字段）
        email: (state) => state.userInfo?.email || '',

        id: state => state.userInfo?.id,
    },
})
