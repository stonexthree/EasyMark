<template>
  <div class="component-root" :style="{width:widthPercent+'%' }">
    <div class="member-area" :style="memberAreaStyle">
      <div :class="{'user-name':true,'selected-user-name':selectedMember === member.name}"
           v-for="member in memberArray"
           @click="clickMember(member.name)">
        {{ member.nickname }}
      </div>
    </div>
    <div class="doc-area">
      <DocList :docs-api-config="docApiConfig" :style="{top:'0px',height: '100%'}" :loading="false"/>
    </div>
  </div>
</template>

<script lang="ts">
export default {
  name: "MemberDoc"
}
</script>

<script setup lang="ts">
import DocList  from "./DocList.vue"
import {DocInfo} from "../model/models"
import {ref, onMounted, reactive,Ref,computed} from "vue";
import axios from 'axios'
import {UserApi, DocApi} from '../api-define'
import {loginStatus} from "../globalStatus";
import {customComponentThemeProvider,ColorSet} from '../theme'

const memberArray = ref<{ name: string, nickname: string }[]>([]);

function loadMember(mock: boolean, mockSize: number): { name: string, nickname: string }[] {
  const result: { name: string, nickname: string }[] = [];
  if (mock) {
    for (let i = 0; i < mockSize; i++) {
      result.push({name: "用户" + i, nickname: "昵称" + i})
    }
  }
  return result;
}

const selectedMember = ref<string>("none")
const docs:Ref<DocInfo[]> = ref([])
const docApiConfig = ref<Object>({});
function clickMember(memberName: string): void {
  selectedMember.value = memberName;
  docs.value = [];
  docApiConfig.value=DocApi.getDocByAuthor(memberName);
  /*axios.request(DocApi.getDocByAuthor(memberName)).then((response) => {
    if (response.data.code === '00000') {
      for(const i in response.data.data){
        const row = response.data.data[i];
        docs.value.push(new DocInfo(row.docId,row.docName,row.authorNickname,row.updateTimestamp));
      }
    }
    if (response.data.code === 'A0200') {
      //loginStatus.loginFailed();
    }
  }).catch();
  console.log(docs.value);*/
}

function getAllUser(){
  //memberArray.value = loadMember(true, 10);
  axios.request(UserApi.getAllUsers()).then((response) => {
    if (response.data.code === '00000') {
      const result: { name: string, nickname: string }[] = [];
      for (const i in response.data.data) {
        const member = response.data.data[i];
        result.push({name: member.userName, nickname: member.userNickname});
      }
      memberArray.value = result;
    }
    if (response.data.code === 'A0200') {
      //loginStatus.loginFailed();
    }
  }).catch()
}

onMounted(
    () => {
      getAllUser();
      loginStatus.registerAction(getAllUser);
    }
)

defineProps({
  loading: Boolean,
  widthPercent: Number
})

////////////////////////
//样式
const memberAreaStyle = computed<any>(()=>{
  return {
    backgroundColor:customComponentThemeProvider.value.colorSet.halfLight
  }
})
const colorSet = computed<ColorSet>(()=>{
  return customComponentThemeProvider.value.colorSet;
})
</script>

<style  scoped>
.component-root {
  position: absolute;
  width: 100%;
  height: calc(100% );
  left: 50%;
  transform: translateX(-50%);
}

.doc-area {
  position: absolute;
  left: 20%;
  width: 80%;
  min-width: 240px;
  height: 100%;
  background-color: transparent;
}

.member-area {
  position: absolute;
  left: 0%;
  width: 20%;
  min-width: 60px;
  height: 100%;
  overflow: scroll;
}
.member-area::-webkit-scrollbar{
  display: none;
}

.user-name {
  height: 40px;
  font-size: 1.2em;
  line-height: 40px;
  padding-left: 10px;
  user-select: none;
}

.user-name {
  color: v-bind(colorSet.fontColor3);
}

.user-name:hover {
  cursor: pointer;
  font-size: 1.5em;
  animation-name: text-shining;
  animation-fill-mode: both;
  animation-duration: 1s;
  animation-iteration-count: infinite;
  animation-direction: alternate;
}

@keyframes text-shining {
  0% {
    color: v-bind(colorSet.fontColor3);
    text-shadow: 0 0 5px v-bind(colorSet.fontColor3);
  }
  100% {
    color: v-bind(colorSet.fontColor);
    text-shadow: 0 0 5px v-bind(colorSet.fontColor);
  }
}

.selected-user-name {
  background-color: v-bind(colorSet.deep);
  text-align: center;
}
</style>