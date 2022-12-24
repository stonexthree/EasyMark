import {darkTheme} from 'naive-ui'

export interface ThemeProvider {
    perSet:any,
    override:any
}

export const polarNightTheme:ThemeProvider = {
    perSet:darkTheme,
    override:{
        "List": {
            "color": "rgba(255, 255, 255, 0)",
            "colorHover": "rgba(101,109,126,1)",
            "textColor": "rgba(200,200,200,1)"
        },
        'DataTable':{
            'tdColor':'rgba(0,0,0,0%)',
        }
    }
}