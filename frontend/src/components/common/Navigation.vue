<template>
  <v-navigation-drawer
    app
    :mini-variant="miniVariant"
    :permanent="permanent"
    :color="'grey lighten-4'"
  >
    <v-list-item class="px-2 mt-2 justify-center">
      <logo class="my-2" :size="miniVariant ? 40 : 100" />
    </v-list-item>

    <v-list>
      <v-list-item v-for="(item, j) in items" :key="j" :to="item.to" :disabled="$store.state.menuDisabled">
        <v-list-item-action>
          <v-icon>{{ item.icon }}</v-icon>
        </v-list-item-action>
        <v-list-item-content>
          <v-list-item-title class="text-left" v-text="item.title" />
        </v-list-item-content>
      </v-list-item>
    </v-list>
    <template v-slot:append>
      <v-list>
        <v-list-item @click.stop="logout" >
          <v-list-item-action>
            <v-icon>mdi-logout</v-icon>
          </v-list-item-action>
          <template v-slot:activator>
          
          </template>
          <v-list-item-content>
            <v-list-item-title class="text-left" v-text="'Log Out'" />
          </v-list-item-content>
          <v-list-item-action>
            <v-btn
              depressed
              x-small
              :right="true"
              :min-height="40"
              @click.stop="miniVariant = !miniVariant"
            >
              <v-icon>{{
                miniVariant
                  ? "mdi-chevron-double-right"
                  : "mdi-chevron-double-left"
              }}</v-icon>
            </v-btn>
          </v-list-item-action>
        </v-list-item>
      </v-list>
      <v-layout class="pa-2 justify-end" v-show="miniVariant">
        <v-btn
          depressed
          x-small
          :right="true"
          :min-height="40"
          @click.stop="toggleMiniVariant"
        >
          <v-icon>{{
            miniVariant ? "mdi-chevron-double-right" : "mdi-chevron-double-left"
          }}</v-icon>
        </v-btn>
      </v-layout>
    </template>
  </v-navigation-drawer>
</template>

<style lang="scss">
.v-list-item .v-avatar .v-image {
  transition: none;
}
.v-icon.v-icon.mdi-chat-outline:after {
  content: "\F039F";
  display: inline-block;
  font: normal normal normal 24px/1 "Material Design Icons";
  height: 13px;
  overflow: hidden;
  width: 15px;
  text-indent: -14px;
  position: absolute;
  left: 7px;
  top: 1px;
  opacity: 1;
  background: transparent;
  transform: none;
  border-radius: 0;
}
.v-avatar {
  .v-image {
    transition: all, 1s;
  }
}
.v-list-item--link:before {
  background-color: #fff;
}
.theme--light.v-list-item--active:hover::before,
.theme--light.v-list-item--active::before {
  opacity: 0 !important;
}
.v-list-item--active,
.v-list-item--active:hover {
  outline-width: 0;
  background: #fff;
  width: calc(100% + 1px);
  z-index: 1;
}
span.logout-text {
  font-size: 16px;
  font-weight: normal;
  letter-spacing: normal;
}
</style>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { routes } from "@/router";
import AuthService from "@/service/AuthService";
import Logo from "../common/Logo.vue";

interface NavItem {
  icon: string;
  title: string;
  to: string;
}

@Component({
  components: {
    Logo,
  },
})
export default class Navigation extends Vue {
  private miniVariant = true;
  private permanent = true;
  items: Array<NavItem> = [];

  mounted() {
    const navItems = routes
      .filter((route) => route.meta && route.meta.navigation)
      .map((route) => {
        const navItem: NavItem = {
          icon: route.meta.navigation.icon,
          title: route.meta.navigation.title,
          to: route.path,
        };
        return navItem;
      });
    this.items = [...this.items, ...navItems];
  }

  toggleMiniVariant(event: any) {
    this.miniVariant = !this.miniVariant;
    
    event.preventDefault();
  }

  logout() {
    this.miniVariant = true;
    AuthService.logout();
    this.$store.commit("logout");
    this.$router.push({ path: "/login" });
  }
}
</script>
