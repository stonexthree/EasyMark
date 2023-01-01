export {UserApi, DocApi, TagApi, SearchApi,PictureApi,UrlConstructor}
const baseURL = '/api/'
const UserApi = {
    'login': (username: string, password: string) => {
        return {
            url: baseURL + 'login',
            method: 'POST',
            headers: {'Content-Type': 'multipart/form-data; '},
            data: {
                username: username,
                password: password
            }
        }
    },
    logout:()=>{
        return {
            url: baseURL + 'logout',
            method: 'GET'
        }
    },
    'getAllUsers': () => {
        return {
            url: baseURL + 'user/all',
            method: 'GET',
        }
    },
    getLoginUserNickname: () => {
        return {
            url: baseURL + 'user/nickname',
            method: 'GET'
        }
    },
    adminCheck: () => {
        return {
            url: baseURL + 'user/admin/check',
            method: 'GET'
        }
    },
    setPassword:(username:string,password:string) =>{
        return {
            url: baseURL + 'user/admin/password',
            method: 'POST',
            headers: {'Content-Type': 'multipart/form-data; '},
            data: {
                username: username,
                password: password
            }
        }
    },
    changePassword:(password:string) =>{
        return {
            url: baseURL + 'user/password',
            method: 'POST',
            headers: {'Content-Type': 'multipart/form-data; '},
            data: {
                newPassword: password
            }
        }
    },
    createAccount:(username:string,password:string) =>{
        return {
            url: baseURL + 'user/detail',
            method: 'POST',
            headers: {'Content-Type': 'multipart/form-data; '},
            data: {
                username: username,
                password: password
            }
        }
    },
    getMyProfile:()=>{
        return{
            url:baseURL + 'user/profile',
            method: 'GET'
        }
    },
    changeNickname:(nickname:string)=>{
        return{
            url:baseURL + 'user/nickname',
            method: 'PUT',
            headers: {'Content-Type': 'multipart/form-data; '},
            data: {
                nickname: nickname
            }
        }
    }
}

const DocApi = {
    'getAllDoc': () => {
        return {
            url: baseURL + 'doc/list',
            method: 'GET'
        }
    },
    'getDocByAuthor': (author: string) => {
        return {
            url: baseURL + 'doc/list/user-doc/' + author,
            method: 'GET'
        }
    },
    'getMyDocs': () => {
        return {
            url: baseURL + 'doc/my-doc',
            method: 'GET'
        }
    },
    'getDocContent': (docId: string) => {
        return {
            url: baseURL + 'doc/markdown/' + docId,
            method: 'GET'
        }
    },
    'newDoc': (docName: string, content: string) => {
        return {
            url: baseURL + 'doc/markdown',
            method: 'POST',
            headers: {'Content-Type': 'multipart/form-data; '},
            data: {
                docName: docName,
                content: content
            }
        }
    },
    'newDocWithTags': (docName: string, content: string, tags: string[]) => {
        return {
            url: baseURL + 'doc/markdown_tags',
            method: 'POST',
            headers: {'Content-Type': 'multipart/form-data; '},
            data: {
                docName: docName,
                content: content,
                'label-name': tags.join(',')
            }
        }
    },
    'modifyDoc': (docId: string, content: string) => {
        return {
            url: baseURL + 'doc/markdown/content/' + docId,
            method: 'PUT',
            headers: {'Content-Type': 'multipart/form-data; '},
            data: {
                content: content
            }
        }
    },
    'changeDocName': (title: string, docId: string) => {
        return {
            url: baseURL + 'doc/markdown/doc-name/' + docId,
            method: 'PUT',
            headers: {'Content-Type': 'multipart/form-data; '},
            data: {
                name: title
            }
        }
    }
}

const TagApi = {
    'deleteDocMap': (docId: string) => {
        return {
            url: baseURL + 'label/map/doc/' + docId,
            method: 'DELETE',
        }
    },
    'setMap': (docId: string, tags: string[]) => {
        return {
            url: baseURL + 'label/map/set',
            method: 'POST',
            headers: {'Content-Type': 'multipart/form-data; '},
            data: {
                'doc-id': docId,
                'label-name': tags.join(',')
            }
        }
    },
    'searchTag': (keyword: string) => {
        return {
            url: baseURL + 'label/keyword/' + keyword,
            method: 'GET'
        }
    }
}

const SearchApi = {
    'searchDoc': (scope: string, keyword: string) => {
        return {
            url: baseURL + 'search/scope/' + scope + '/' + keyword,
            method: 'GET',
        }
    },
}

const PictureApi = {
    upload:(form:FormData) => {
        return {
            url: baseURL + 'files/upload',
            method: 'POST',
            data: form
        }
    }
}

const UrlConstructor = {
    pictureUrl:(name:string):string=>{
        return '/picture/'+name;
    }
}

