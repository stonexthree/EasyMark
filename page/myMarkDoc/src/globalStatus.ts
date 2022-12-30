import {reactive, ref, Ref} from 'vue'
import {DocInfo} from './model/models'
import {AxiosRequestConfig} from 'axios'

export const loginStatus = reactive(
    {
        isLogin: true,
        standardProfile: {
            name: '',
            nickname: '',
            createTime: '',
            clear(){
                this.name='';
                this.nickname='';
                this.createTime=''
            }
        },
        statusChanged: false,
        afterLoginSuccessAction: <Function[]>[],
        afterLoginSuccessActionTemp: <Function[]>[],//每次被调用后都会被清空
        //注册登录成功后的回调
        registerAction(action: Function, always: boolean = false) {
            if (always) {
                this.afterLoginSuccessAction.push(action);
                return;
            }
            this.afterLoginSuccessActionTemp.push(action);
        },
        loginFailed(): void {
            console.log(this);
            this.isLogin = false;
            this.standardProfile.clear();
            this.statusChanged = true;
        },
        loginSuccess(): void {
            console.log('-----------login success---------');
            this.isLogin = true;
            //this.profile.name = username;
            this.statusChanged = true;
            //console.log(this.loginUser);
            console.log('----------------------------');
            for (let i in this.afterLoginSuccessAction) {
                console.log('exec: '+ this.afterLoginSuccessAction[i].name)
                this.afterLoginSuccessAction[i]();
            }
            console.log('----------------------------');
            for (let i in this.afterLoginSuccessActionTemp) {
                console.log('exec: '+ this.afterLoginSuccessActionTemp[i].name)
                this.afterLoginSuccessActionTemp[i]();
            }
            this.afterLoginSuccessActionTemp = [];//清空
        }
    }
)
//export const showSearchTool:Ref<boolean> = ref(false);
export const searchStatus: Ref<{ showSearchTool: boolean, lastSearchResult: DocInfo[],searchApi:AxiosRequestConfig<any> }> = ref({
    showSearchTool: false,
    lastSearchResult: [],
    searchApi: {}
})