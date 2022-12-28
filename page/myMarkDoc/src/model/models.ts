export class DocInfo {
    docId: string;
    docName: string;
    docAuthor: string;
    updateTimestamp: string;

    constructor(docId:string,docName: string, docAuthor: string, updateTimestamp: number) {
        this.docName = docName;
        this.docAuthor = docAuthor;
        this.updateTimestamp = new Date(updateTimestamp).toLocaleDateString();
        this.docId = docId;
    }

    static factory(docId:string,docName: string, docAuthor: string, updateTimestamp: number):DocInfo {
        return new DocInfo(docId,docName, docAuthor, updateTimestamp);
    }

    static mockArray(size: Number): DocInfo[] {
        const result: DocInfo[] = [];
        for (let i: number = 0; i < size; i++) {
            const docId : string = i.toString();
            const docName: string = "文档" + i;
            const docAuthor: string = "作者" + i;
            const updateTimestamp: number = new Date().getTime();
            result.push(new DocInfo(docId,docName, docAuthor, updateTimestamp));
        }
        return result;
    }
}

export interface ImageAction{
    title:string,
    action:Function
}

export type SearchScope = 'name'|'label'|'composite'