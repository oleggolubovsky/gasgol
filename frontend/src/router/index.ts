import Vue from "vue";
import VueRouter, { RouteConfig } from "vue-router";
import Home from "../views/Home.vue";
import store from "../store/index";

Vue.use(VueRouter);

export const routes: Array<RouteConfig> = [
  {
    path: "/",
    component: Home,
    meta: {
      requiresLogin: true,
      navigation: {
        icon: 'mdi-home-roof',
        title: 'Home'
      }
    }
  },
  {
    path: "/contact-list",
    component: () =>
      import("../views/contactList/List.vue"),
    meta: {
      requiresLogin: true,
      navigation: {
        icon: 'mdi-format-list-bulleted-square',
        title: 'Contact List'
      }
    }
  },
  {
    path: "/contact-list/:id",
    component: () =>
      import("../views/contactList/View.vue"),
    meta: {
      requiresLogin: true,
    }
  },
  {
    path: "/messaging-campaigns",
    name: "Messaging Campaigns",
    component: () =>
      import("../views/messagingCampaigns/List.vue"),
    meta: {
      requiresLogin: true,
      navigation: {
        icon: 'mdi-chat-outline',
        title: 'Messaging'
      }
    }
  },
  {
    path: "/messaging-campaigns/new",
    name: "new Messaging Campaigns",
    component: () =>
      import("../views/messagingCampaigns/AddNewMessaging.vue"),
    meta: {
      requiresLogin: true
    }
  },
  {
    path: "/messaging-campaigns/:id",
    component: () =>
      import("../views/messagingCampaigns/AddNewMessaging.vue"),
    meta: {
      requiresLogin: true,
    }
  },
  {
    path: "/message-templates",
    name: "Message Templates",
    component: () =>
      import("../views/messageTemplates/List.vue"),
    meta: {
      requiresLogin: true,
      navigation: {
        icon: 'mdi-comment-text-outline',
        title: 'Templates'
      }
    }
  },
  {
    path: "/user-list",
    component: () =>
      import("../views/userList/List.vue"),
    meta: {
      requiresLogin: true,
      navigation: {
        icon: 'mdi-account-supervisor',
        title: 'User List'
      }
    }
  },
  {
    path: "/user-list/:id",
    component: () =>
      import("../views/userList/New.vue"),
    meta: {
      requiresLogin: true,
    }
  },
  {
    path: "/login",
    name: "Login",
    component: () =>
      import("../views/Login.vue")
  },
  {
    path: "/register",
    name: "Registration",
    component: () =>
      import("../views/registration/Registration.vue")
  },
  {
    path: "/users/confirm/:token",
    name: "Create Password",
    component: () =>
      import("../views/registration/CreatePassword.vue")
  },
  {
    path: "/terms-of-service",
    name: "Terms of Service",
    component: () =>
      import("../views/userAgreement/TermsOfService.vue")
  },
  {
    path: "/privacy-policy",
    name: "Privacy Policy",
    component: () =>
      import("../views/userAgreement/PrivacyPolicy.vue")
  },
  {
    path: "/acceptable-use-policy",
    name: "Acceptable Use Policy",
    component: () =>
      import("../views/userAgreement/AcceptableUsePolicy.vue")
  }
];

export const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes
});

router.beforeEach((to, from, next) => {
  if (to.matched.some(record => record.meta.requiresLogin) && !store.state.user.authenticated) {
    next("/register")
  } else {
    next()
  }
})
