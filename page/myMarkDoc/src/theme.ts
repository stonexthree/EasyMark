import {darkTheme} from 'naive-ui'
import {ref,Ref} from 'vue'
import { themeFactory, ThemeColor, ThemeSize, ThemeIcon, ThemePlugin,ThemeBorder } from '@milkdown/core';
import {nordDark} from '@milkdown/theme-nord';
/**
 * naive-ui的主题控制接口
 */
export interface ThemeProvider {
    perSet:any,
    override:any
}
export interface ColorSet{
    fontColor:string,
    fontColor2:string,
    fontColor3:string,
    fontColor4:string,
    deep:string,
    halfDeep:string,
    halfLight:string,
    light:string,
    heightLight1:string,
    heightLight2:string,
    heightLight3:string,
    heightLight4:string,
    extension1:string,
    extension2:string
}
export type ThemeName = 'NordDark';

//Nord Polar Night 主题
const nordPolarNightColorSet:ColorSet={
    fontColor:'rgba(255,255,255,1)',
    fontColor2:'rgba(220,220,220,1)',
    fontColor3:'rgba(200,200,200,1)',
    fontColor4:'rgba(140,140,140,1)',
    //rgba(46,52,64,1)
    deep:'#2E3440',
    //rgba(59,66,82,1)
    halfDeep:'#3B4252',
    //rgba(67,76,94,1)
    halfLight:'#434C5E',
    //rgba(76,86,106,1)
    light:'#4C566A',
    heightLight1:'#1C2434',
    heightLight2:'#4C566A',
    heightLight3:'#4C566A',
    heightLight4:'#4C566A',
    extension1:'#1E92A0FF',
    extension2:'#4C566A'
};
const nordPolarNightTheme:ThemeProvider = {
    perSet:darkTheme,
    override:{
        "List": {
            "color": "rgba(255, 255, 255, 0)",
            "colorHover": "#4C566A",
            "textColor": "rgba(220,220,220,1)"
        },
        'DataTable':{
            'thColor':'rgba(59,66,82,1)',//表头
            'tdColor':'rgba(67,76,94,0)',//数据行颜色
            'tdColorStriped':'rgba(67,76,94,0.1)',//数据行条纹色
            'borderColor': 'rgba(59,66,82,1)'
        },
        'Modal':{
            'boxShadow': '0px 0px transparent'
        },
        'Button':{
            'textColor': 'rgba(200,200,200,100%)',
            'border': '1px solid rgba(200,200,200,50%)'
        }
    }
};
const nordEditorTheme = nordDark.override((emotion, manager) => {
    manager.set(ThemeColor, ([key, opacity]) => {
        switch (key) {
            case 'primary':
                return `rgba(200, 200, 200, ${opacity})`;
            case 'secondary':
                return `rgba(220, 220, 220, ${opacity})`;
            case 'neutral':
                return `rgba(220, 220, 220, ${opacity})`;
            case 'solid':
                return `rgba(220, 220, 220, ${opacity})`;
            case 'shadow':
                return `rgba(59, 66, 82, ${opacity})`;
            case 'line':
                return `rgba(200,200,200, ${opacity})`;
            case 'surface':
                return `rgba(59, 66, 82, 1)`;
            case 'background':
                return `rgba(46, 52, 64, ${opacity})`;
            // ...
            default:
                return `rgba(0,0,255, ${opacity})`;
        }
    });
});

//默认采用 Nord Polar Night
export const customComponentThemeProvider:Ref<{colorSet:ColorSet,naiveUITheme:ThemeProvider,editorTheme: ThemePlugin}> = ref({
    colorSet:nordPolarNightColorSet,
    naiveUITheme:nordPolarNightTheme,
    editorTheme:nordEditorTheme
})
