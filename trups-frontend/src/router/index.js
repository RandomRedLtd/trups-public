import { createRouter, createWebHistory } from 'vue-router/auto'

import Models from "@/pages/Models.vue"
import Model from "@/pages/Model.vue"
import NewModel from "@/pages/NewModel.vue"
import Settings from "@/pages/Settings.vue"
import Policy from "@/pages/Policy.vue"

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: "/",
            name: "Models",
            component: Models
        },
        {
            path: "/models/:id",
            name: "Model",
            component: Model
        },
        {
            path: "/newmodel",
            name: "New Model",
            component: NewModel
        },
        {
            path: "/settings",
            name: "Settings",
            component: Settings
        },
        {
            path: "/policy",
            name: "Policy",
            component: Policy
        }
    ],
})

// Workaround for https://github.com/vitejs/vite/issues/11804
router.onError((err, to) => {
    if (err?.message?.includes?.('Failed to fetch dynamically imported module')) {
        if (!localStorage.getItem('vuetify:dynamic-reload')) {
            console.log('Reloading page to fix dynamic import error')
            localStorage.setItem('vuetify:dynamic-reload', 'true')
            location.assign(to.fullPath)
        } else {
            console.error('Dynamic import error, reloading page did not fix it', err)
        }
    } else {
        console.error(err)
    }
})

router.isReady().then(() => {
    localStorage.removeItem('vuetify:dynamic-reload')
})

export default router
