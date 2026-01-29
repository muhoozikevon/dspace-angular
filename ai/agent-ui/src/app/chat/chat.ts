import {Component} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HttpClient, HttpDownloadProgressEvent, HttpEventType} from '@angular/common/http';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [
    FormsModule
  ],
  templateUrl: './chat.html',
  styleUrls: ['./chat.css']
})
export class Chat {
  query: string="";
  response : any;
  progress :boolean = false;

  constructor(private http : HttpClient) {
  }

  askAgent() {
    this.response="";
    this.progress = true;
    this.http.get("http://localhost:8080/askAgent?query="+this.query,
      {responseType :'text',observe:"events", reportProgress:true}).subscribe({
        next : event => {
          if(event.type===HttpEventType.DownloadProgress){
            this.response = (event as HttpDownloadProgressEvent).partialText;
          }
        },
        error : error => {
          console.log(error);
        }
        ,
      complete: () => {
          this.progress = false;
      }
    }
    )
  }
}
