<template>
<div class="options-panel" >
  <div v-for="option in options" :class="router.currentRoute.value.path === option.routePath ?'selected':''"
       @click="clickOption(option.routePath,option.optionsKey)">{{option.routeName}}</div>
</div>
</template>

<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'
import {ref,Ref,onMounted,computed} from "vue";
import axios from 'axios'
import {loginStatus} from '../globalStatus'
import {UserApi} from '../api-define'
import {customComponentThemeProvider,ColorSet} from "../theme";


const router = useRouter()
const options:Ref<{optionsKey:string,routeName:string,routePath:string}[]> = ref([])
const commonOptions:{optionsKey:string,routeName:string,routePath:string}[] = [
  { optionsKey: 'docList',routeName: '文档列表', routePath: '/doc-list'},
  {optionsKey: 'memberDoc',routeName: '成员文档', routePath: '/member-doc'},
  {optionsKey:'myDoc',routeName: '我的文档', routePath: '/my-doc'},
  {optionsKey: 'newDoc',routeName: '编写文档', routePath: '/new-doc'},
]
const adminOptions:{optionsKey:string,routeName:string,routePath:string}[] = [
  {optionsKey:'accountManagement',routeName: '账户管理', routePath: '/account-management'},
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

const currentOption:Ref<string> = ref('')
function clickOption(path:string,op:string){
  router.push(path);
  currentOption.value = op;
}

onMounted(()=>{
  adminCheck();
  loginStatus.registerAction(adminCheck,true);
})

const colorSet = computed<ColorSet>(()=>{
  return customComponentThemeProvider.value.colorSet;
})

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
  top: 100px;
  left: 60px;
  border-style: solid;
  border-color: v-bind(colorSet.halfLight);
  border-width: 0px 0px 0px 0px;
  font-size: 1em;
}
.options-panel::-webkit-scrollbar{
  display: none;
}
.options-panel > div{
  width: 100%;
  color: v-bind(colorSet.fontColor4);
  font-size: 1em;
  padding: 3px 32px 3px 32px;
  margin:  0px;
  user-select: none;
  border-style: solid;
  border-color: v-bind(colorSet.halfLight);
  border-width: 0px 0px 0px 4px;
}
.options-panel > div:hover{
  cursor: pointer;
  padding-left: 48px;
  color: v-bind(colorSet.fontColor2);
  font-size: 1.2em;
}

.options-panel > .selected {
  color: v-bind(colorSet.fontColor2);
  border-color: v-bind(colorSet.extension1);
  background-color: v-bind(colorSet.halfDeep);
}
</style>