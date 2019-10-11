package com.gykj.zhumulangma.common.net;

import com.gykj.zhumulangma.common.net.dto.GitHubDTO;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface UserService {
    String HOST1=API.BaseUrl.KEY+":"+API.BaseUrl.HOST1;


    @GET(API.GITHUB_URL)
    Observable<GitHubDTO> getGitHub();
}
