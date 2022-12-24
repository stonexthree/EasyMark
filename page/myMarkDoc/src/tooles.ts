const blankStringList:string[] = ['&#x20;','&#x09;','&#x0D;','&#x0A;']
export function blankTransport(str:string):string{
    let result = str;
    if(str.length<=6){
        return str;
    }
    for(let i in blankStringList){
        result = result.replace(blankStringList[i],' ');
    }
    return result;
}