package com.example.cherrypickserver.article.dto.response;

import com.example.cherrypickserver.article.domain.ArticlePhoto;
import lombok.Builder;
import lombok.Data;

@Data
public class DetailArticlePhotoRes {
  private String articleImgUrl;
  private String imgDesc;

  @Builder
  public DetailArticlePhotoRes(String articleImgUrl, String imgDesc) {
    this.articleImgUrl = articleImgUrl;
    this.imgDesc = imgDesc;
  }

  public static DetailArticlePhotoRes toDto(ArticlePhoto articlePhoto) {
    return DetailArticlePhotoRes.builder()
            .articleImgUrl(articlePhoto.getArticleImgUrl())
            .imgDesc(articlePhoto.getImgDesc())
            .build();
  }
}
