import {darkTheme} from 'naive-ui'
import {ref, Ref} from 'vue'
import {
    ThemeColor,
    ThemePlugin,
    ThemeManager
} from '@milkdown/core';
import {getTheme} from './themeToole'
/**
 * naive-ui的主题控制接口
 */
export interface ThemeProvider {
    perSet: any,
    override: any
}

export interface ColorSet {
    fontColor: string,
    fontColor2: string,
    fontColor3: string,
    fontColor4: string,
    deep: string,
    halfDeep: string,
    halfLight: string,
    light: string,
    heightLight1: string,
    heightLight2: string,
    heightLight3: string,
    heightLight4: string,
    extension1: string,
    extension2: string
}

export type ThemeName = 'NordPolarNight' | 'NordSnowStorm';

//Nord Polar Night 主题
const nordPolarNightColorSet: ColorSet = {
    fontColor: 'rgba(255,255,255,1)',
    fontColor2: 'rgba(220,220,220,1)',
    fontColor3: 'rgba(200,200,200,1)',
    fontColor4: 'rgba(140,140,140,1)',
    //rgba(46,52,64,1)
    deep: '#2E3440',
    //rgba(59,66,82,1)
    halfDeep: '#3B4252',
    //rgba(67,76,94,1)
    halfLight: '#434C5E',
    //rgba(76,86,106,1)
    light: '#4C566A',
    heightLight1: '#9C946B',
    heightLight2: '#4C566A',
    heightLight3: '#4C566A',
    heightLight4: '#4C566A',
    extension1: '#1E92A0FF',
    extension2: '#191C21'
};
const nordPolarNightTheme: ThemeProvider = {
    perSet: darkTheme,
    override: {
        "List": {
            "color": "rgba(255, 255, 255, 0)",
            "colorHover": "#4C566A",
            "textColor": "rgba(220,220,220,1)"
        },
        'DataTable': {
            'thColor': 'rgba(59,66,82,1)',//表头
            'tdColor': 'rgba(67,76,94,0.6)',//数据行颜色
            'tdColorStriped': 'rgba(67,76,94,1)',//数据行条纹色
            'borderColor': 'rgba(59,66,82,1)',
            'tdColorHover':'rgba(76,86,106,1)'
        },
        'Modal': {
            'boxShadow': '0px 0px transparent'
        },
        'Button': {
            'textColor': 'rgba(200,200,200,100%)',
            'border': '1px solid rgba(200,200,200,50%)'
        }
    }
};
const nordPolarNightEditorTheme = getTheme((manager:ThemeManager)=>{
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
                return `rgba(100,100,100, ${opacity})`;
            case 'surface':
                return `rgba(59, 66, 82, 1)`;
            case 'background':
                return `rgba(46, 52, 64, ${opacity})`;
            // ...
            default:
                return `rgba(0,0,255, ${opacity})`;
        }
    });
})

//Nord Snow Storm 主题
const nordSnowStormColorSet: ColorSet = {
    fontColor: 'rgba(20,20,20,1)',
    fontColor2: 'rgba(40,40,40,1)',
    fontColor3: 'rgba(80,80,80,1)',
    fontColor4: 'rgba(100,100,100,1)',
    //rgba(216,222,233,1)
    deep: '#D8DEE9',
    //rgba(229,233,240,1)
    halfDeep: '#E5E9F0',
    //rgba(236,239,244,1)
    halfLight: '#ECEFF4',
    //rgba(245,246,249,1)
    light: '#F5F6F9',
    heightLight1: '#AEBACF',
    heightLight2: '#7A8CAB',
    heightLight3: '#B8B2D3',
    heightLight4: '#8A80B2',
    extension1: '#7A8CAB',
    extension2: '#AEBACF'
};
const nordSnowStormTheme: ThemeProvider = {
    perSet: null,
    override: {
        "List": {
            "color": "rgba(0, 0, 0, 0)",
            "colorHover": "#ECEFF4",
            "textColor": "rgba(40,40,40,1)"
        },
        'DataTable': {
            'thColor': 'rgba(229,233,240,1)',//表头
            'tdColor': 'rgba(236,239,244,0.5)',//数据行颜色
            'tdColorStriped': 'rgba(236,239,244,1)',//数据行条纹色
            'borderColor': '#E5E9F0',
            'tdColorHover': 'rgba(245,246,249,1)'
        },
        'Modal': {
            'boxShadow': '0px 0px transparent'
        },
        'Button': {
            'textColor': 'rgba(40,40,40,100%)',
            'border': '1px solid rgba(40,40,40,50%)'
        }
    }
};
const nordSnowStormEditorTheme = getTheme((manager:ThemeManager)=>{
    manager.set(ThemeColor, ([key, opacity]) => {
        switch (key) {
            case 'primary':
                return `rgba(80, 80, 80, ${opacity})`;
            case 'secondary':
                return `rgba(40, 40, 40, ${opacity})`;
            case 'neutral':
                return `rgba(40, 40, 40, ${opacity})`;
            case 'solid':
                return `rgba(40, 40, 40, ${opacity})`;
            case 'shadow':
                return `rgba(229,233,240, ${opacity})`;
            case 'line':
                return `rgba(210,210,210, ${opacity})`;
            case 'surface':
                return `rgba(229, 233, 240, 1)`;
            case 'background':
                return `rgba(216,222,233, ${opacity})`;
            // ...
            default:
                return `rgba(0,0,255, ${opacity})`;
        }
    });
})

//默认采用 Nord Polar Night
export const customComponentThemeProvider: Ref<{ colorSet: ColorSet, naiveUITheme: ThemeProvider, editorTheme: ThemePlugin }> = ref({
    colorSet: nordPolarNightColorSet,
    naiveUITheme: nordPolarNightTheme,
    editorTheme: nordPolarNightEditorTheme
})

function changeTheme(name: ThemeName): void {
    switch (name) {
        case "NordPolarNight": {
            customComponentThemeProvider.value = {
                colorSet: nordPolarNightColorSet,
                naiveUITheme: nordPolarNightTheme,
                editorTheme: nordPolarNightEditorTheme
            }
        }
            ;
            break;
        case "NordSnowStorm": {
            customComponentThemeProvider.value = {
                colorSet: nordSnowStormColorSet,
                naiveUITheme: nordSnowStormTheme,
                editorTheme: nordSnowStormEditorTheme
            }
        }
            ;
            break;
        default: {
            customComponentThemeProvider.value = {
                colorSet: nordSnowStormColorSet,
                naiveUITheme: nordSnowStormTheme,
                editorTheme: nordSnowStormEditorTheme
            }
        }
    }
}

export const ThemeStatus: Ref<{ currentTheme: ThemeName, themes: ThemeName[], change: (name: ThemeName) => void, next: Function }> = ref({
    currentTheme: <ThemeName>'NordPolarNight',
    themes: <ThemeName[]>['NordPolarNight', "NordSnowStorm"],
    change: function (name: ThemeName) {
        ThemeStatus.value.currentTheme = name;
        changeTheme(name);
    },
    next: function () {
        console.log(1);
        let nextIndex: number = 0;
        for (let i in ThemeStatus.value.themes) {
            if (ThemeStatus.value.themes[i] === ThemeStatus.value.currentTheme) {
                let ci = Number.parseInt(i);
                nextIndex = ci === ThemeStatus.value.themes.length - 1 ? 0 : ++ci;
                break;
            }
        }
        ThemeStatus.value.currentTheme = ThemeStatus.value.themes[nextIndex];
        changeTheme(ThemeStatus.value.themes[nextIndex]);
    }
});