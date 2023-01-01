<template>
  <div :class="{'component-root':true,extend:detailUrl!==''}">
    <div id="sketch-div">
      <div class="image-item" v-for="url in pictureUrls">
        <img :src="url" @click="clickSketch(url)" />
      </div>
      <div class="image-item" id="add-icon" @click="clickUpload">
        <n-icon size="100" :color="halfDeep">
          <Add/>
        </n-icon>
      </div>
    </div>
    <div id="detail-div">
      <div id="detail-image-div" >
        <img id="detail-image" :src="detailUrl" v-show="!showNothing"/>
        <n-empty id="empty-image" description="点击左侧图片" size="huge" v-show="showNothing">
          <template #icon >
            <n-icon>
              <BrokenImageOutlined />
            </n-icon>
          </template>
        </n-empty>
      </div>
      <div id="action-div">
        <div id="url-display">{{detailUrl}}</div>
        <div id="button-div">
        <n-button class="action-button" v-for="ex in extendActions" @click="ex.action(detailUrl)">{{ ex.title }}</n-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
export default {
  name: "PictureUpload"
}

</script>

<script setup lang="ts">
import {Ref, ref,computed} from 'vue'
import {NImage, NIcon,NDivider,NEmpty,NButton} from 'naive-ui'
import {Add} from '@vicons/carbon'
import {BrokenImageOutlined} from '@vicons/material'
import {ImageAction} from '../model/models'
import axios from 'axios'
import {PictureApi,UrlConstructor} from '../api-define'
import {loginStatus} from '../globalStatus'
import {customComponentThemeProvider,ColorSet} from '../theme'

const pictureUrls: Ref<string[]> = ref([]);
const detailUrl: Ref<string> = ref('');
const showNothing: Ref<boolean> = ref(true);

/*function mockPictures(initSize: number): void {
  const url = 'http://127.0.0.1:8081/picture/18f2f437-21d0-4a94-97a9-07dd2a9798e7.jpg'
  for (let i: number = 0; i < initSize; i++) {
    pictureUrls.value.push(url);
  }
}*/
//mockPictures(20);

function clickSketch(url:string){
  showNothing.value = false;
  detailUrl.value = url;
}

defineProps({
  extendActions: Array<ImageAction>
})

// const formElement = document.createElement('form');

function clickUpload():void{
  const fileSelect = document.createElement('input');
  fileSelect.type = 'file';
  fileSelect.multiple=true;
  fileSelect.onchange=function (e:any){
    const fileArr:Array<Blob> = e.target.files;
    const form = new FormData();
    for(let i in fileArr){
      const file:Blob = fileArr[i];
      form.append('file',file);
    }
    axios.request(PictureApi.upload(form)).then((response)=> {
      for(let i in response.data.data){
        const name = <string> response.data.data[i];
        pictureUrls.value.push(UrlConstructor.pictureUrl(name));
      }
    }).catch((response)=>console.log(response.data));
  }
  fileSelect.click();
}
//重新登录会清空上传记录
loginStatus.registerAction(()=>{
  pictureUrls.value=[];
  detailUrl.value='';
},true);


/////////////////////
//样式
const colorSet = computed<ColorSet>(()=>{
  return customComponentThemeProvider.value.colorSet;
})
const halfDeep = computed<any>(()=>{
  return customComponentThemeProvider.value.colorSet.halfDeep;
})
</script>

<style scoped>
.component-root {
  position:absolute;
  width: 372px;
  height: 400px;
}
#sketch-div{
  position: absolute;
  left: 0px;
  top:0px;
  height: 400px;
  width: 372px;
  overflow-y: scroll;
  background-color: v-bind(colorSet.light);
}
#sketch-div::-webkit-scrollbar{
  display: none;
}

#sketch-div .image-item {
  display: inline-block;
  background-color: transparent;
  width: 100px;
  height: 100px;
  margin: 10px;
  border-width: 2px;
  border-color: v-bind(colorSet.halfDeep);
  border-radius: 10px;
  border-style: solid;
  overflow: hidden;
}

.image-item img{
  width: 100px;
  height: 100px;
}

#sketch-div #add-icon {
  border-style: dashed;
}

#detail-div{
  margin-left: 10px;
  position: absolute;
  left: 382px;
  top:0px;
  height: 400px;
  width: calc(100% - 372px);
  background-color: v-bind(colorSet.light);
  overflow: hidden;
}

.extend{
  animation-name: extend;
  animation-duration: 1s;
  animation-fill-mode: both;
}

#detail-div #detail-image-div{
  position: absolute;
  width: 380px;
  height: 380px;
  left: 10px;
  top:10px;
  background-color: transparent;
}
#empty-image{
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%,-50%);
}

#action-div{
  position: absolute;
  left: 400px;
  top:10px;
  width: calc(100% - 400px);
  height: 380px;
  overflow: hidden;
  color: v-bind(colorSet.fontColor4)
}
#url-display{
  position: absolute;
  width: 80%;
  height: 200px;
  top:0px;
  overflow-y: scroll;
  word-break: break-all;

  left: 50%;
  transform: translateX(-50%);
  padding: 4px;
}
#url-display::-webkit-scrollbar{
  display: none;
}
#action-div #button-div{
  position: absolute;
  height: 160px;
  top: 220px;
  width: 100%;
  overflow-y: hidden;
}

#action-div .action-button{
  position: relative;
  width: 150px;
  height: 30px;
  left: 50%;
  transform: translateX(-50%);
  margin: 30px 0px 30px 0px;
  text-align: center;
}

#detail-image{
  width: 380px;
  height: 380px;
}

@keyframes extend {
  0%{
    width: 372px;
  }
  100%{
    width: 972px;
  }
}

</style>