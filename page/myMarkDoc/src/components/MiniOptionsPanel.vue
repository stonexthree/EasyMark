<template>
<div class="options-panel" >
  <div v-for="option in options" :class="option.optionsKey===currentOption?'selected':''"
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
  left: 48px;
  border-style: solid;
  border-color: v-bind(colorSet.halfLight);
  border-width: 0px 0px 0px 4px;
  font-size: 1em;
}
.options-panel > div{
  width: 100%;
  color: v-bind(colorSet.fontColor4);
  font-size: 1em;
  padding-left: 32px;
  margin: 3px;
  user-select: none;
}
.options-panel > div:hover{
  cursor: pointer;
  padding-left: 48px;
  text-shadow:0 0 5px v-bind(colorSet.fontColor2);
  color: v-bind(colorSet.fontColor2);
}

.options-panel > .selected {
  text-shadow:0 0 5px v-bind(colorSet.fontColor2);
  color: v-bind(colorSet.fontColor2);
}
</style>