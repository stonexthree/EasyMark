<template>
<div class="options-panel" >
  <div v-for="option in options" @click="router.push(option.routePath)">{{option.routeName}}</div>
</div>
</template>

<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'
import {ref,Ref,onMounted} from "vue";
import axios from 'axios'
import {loginStatus} from '../globalStatus'
import {UserApi} from '../api-define'
const router = useRouter()
const options:Ref<{routeName:string,routePath:string}[]> = ref([])
const commonOptions:{routeName:string,routePath:string}[] = [
  {routeName: '文档列表', routePath: '/doc-list'},
  {routeName: '成员文档', routePath: '/member-doc'},
  {routeName: '我的文档', routePath: '/my-doc'},
  {routeName: '编写文档', routePath: '/new-doc'},
]
const adminOptions:{routeName:string,routePath:string}[] = [
  {routeName: '账户管理', routePath: '/account-management'},
]

function adminCheck(){
  options.value = [];
  for(let i in commonOptions){
    options.value.push(commonOptions[i]);
  }
  axios.request(UserApi.adminCheck()).then(
      (response)=>{
        if(response.data.code === '00000'){
          for(let i in adminOptions){
            options.value.push(adminOptions[i]);
          }
        }
      }
  ).catch()
}

onMounted(()=>{
  adminCheck();
  loginStatus.registerAction(adminCheck,true);
})
/*defineProps({
  'options':Array<{
    routeName:string,
    routePath:string
  }>
})*/

</script>
<script lang="ts">
export default {
  name: "MiniOptionsPanel"
}
</script>

<style scoped>
.options-panel{
  overflow: scroll;
  position: absolute;
  top: 80px;
  left: 48px;
  border-style: solid;
  border-color: #2e3440;
  border-width: 0px 0px 0px 2px;
}
.options-panel > div{
  width: 100%;
  color: rgba(160,160,160,1);
  font-size: 1em;
  padding-left: 32px;
  margin: 3px;
  user-select: none;
}
.options-panel > div:hover{
  cursor: pointer;
  padding-left: 48px;
  text-shadow:0 0 5px rgb(200,200,200);
  color: rgb(200,200,200);
}
</style>