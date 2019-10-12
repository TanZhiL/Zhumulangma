package com.gykj.zhumulangma.common.net;

import com.gykj.zhumulangma.common.net.dto.GitHubDTO;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface UserService{

    @GET(Api.GITHUB_URL)
    Observable<GitHubDTO> getGitHub();
}
