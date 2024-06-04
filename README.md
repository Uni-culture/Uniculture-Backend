<h1 align="center">UniCulture</h1>

<div align="center">
  🗣️👂📝🌍🤝
</div>

<div align="center">
  <strong>HSU 2024 Capstone Project</strong>
</div>

<div align="center">
  <strong>"Let's be friends!" 대학생 글로벌 언어 교류 커뮤니티</strong>
</div>


<div align="center">
  <h3>
    <a href="https://github.com/capstone-aloha/aloharoom-frontend">
      🖼️ Frontend
    </a>
    <span> | </span>
    <a href="https://github.com/2E2I/mamomo-server">
      🌏 Backend
    </a>
    <span> | </span>
    <a href="https://cut-prune-d8b.notion.site/API-9e0aa8a740524b0ebf45c52894c15d1c?pvs=4">
      📜 REST API 명세서
    </a>
  </h3>
</div>
<br>

## 🔖 목차
- [개요](https://github.com/Uni-culture/Uniculture-Frontend#-개요)
- [실행 및 설치 방법](https://github.com/Uni-culture/Uniculture-Frontend#-실행-및-설치-방법)
- [핵심 기능](https://github.com/Uni-culture/Uniculture-Frontend#-핵심-기능)
    * [빅데이터를 활용한 맞춤형 친구 추천 기능](https://github.com/Uni-culture/Uniculture-Frontend#빅데이터를-활용한-맞춤형-친구-추천-기능)
    * [채팅 속 편의 기능](https://github.com/Uni-culture/Uniculture-Frontend#채팅)
    * [다국어 지원](https://github.com/Uni-culture/Uniculture-Frontend#-다국어-지원)
- [기술 스택](https://github.com/Uni-culture/Uniculture-Frontend#-기술-스택)
- [시스템 구조도](https://github.com/Uni-culture/Uniculture-Frontend#시스템-구조도)
- [주요 화면](https://github.com/Uni-culture/Uniculture-Frontend#-주요-화면)
- [팀 정보](https://github.com/Uni-culture/Uniculture-Frontend#-팀-정보)
- [판넬](https://github.com/Uni-culture/Uniculture-Frontend#-판넬)


## 📍 개요
학교생활을 하다 보면 다양한 국적의 학생들이 보이지만 주로 같은 언어를 사용하는 학생들끼리만 어울리는 모습을 볼 수 있다.<br>
이러한 분리는 언어적인 어려움과 더불어 서로를 연결할 수 있는 적절한 수단이 부족하기 때문에 발생한다.<br>
우리는 이러한 문제점을 해소하기 위해 자신의 취향을 입력하면 그에 맞는 친구를 추천해주고 일상 공유, 스터디, 채팅 등을 통해 쉽게 언어를 교류할 수 있는 웹 서비스를 제공하려고 한다.<br>
평소 다른 나라의 언어와 문화 교류에 관심이 있는 학생들이 쉽게 참여할 수 있도록 접근성을 높여 언어 사용 능력 향상과 대학 내 다문화 공동체의 활성화에 기여하고자 한다.


## 🏃 실행 및 설치 방법
1. Clone the repository
   ```shell
   git clone https://github.com.git
   ```
2. Install NPM packages and run
    ```shell
    $ npm install
    $ npm start
    ```

## ✨ 핵심 기능

### 빅데이터를 활용한 맞춤형 친구 추천 기능

- 사용자의 프로필 정보를 바탕으로 친구 추천
  - 가입 목적, 관심사, 언어 정보
- PyTorch 의 Cosine Similarity 를 통한 각 항목별 가중치를 부여하여 추천 기능 제공
- 1일 내에 추천 친구 재요청 시 기존의 Caching 된 데이터를 불러와서 제공

### 다국어 지원

- 글의 내용을 사용자의 모국어로 번역
  - 게시글, 댓글 번역
  - 채팅 번역 
- 사용자 편의를 위한 번역기 제공


### 채팅

- 상대방의 문장 속 문법 오류 수정 가능
- 랜덤 채팅: 선택한 특정 언어를 할 수 있는 사용자와 무작위로 매칭

### 그 외 

- 게시물, 스터디, 프로필 등 다수의 편의 기능 제공
<br>

## 📜 REST API 명세서
<details>
  <summary>주요 화면 보기</summary>
<img width="800" alt="📜 REST API 명세서" src="https://github.com/JongTKim/Algorithm/assets/95161602/85f3ecf8-0c77-41bf-96ea-a1be34ce96a0">
</details>
<br>

## 📌 기술 스택
<div>
<table>
   <tr>
      <td colspan="2" align="center">
        Language
      </td>
      <td colspan="4">
        <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">
        <img src="https://img.shields.io/badge/javascript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black">
        <img src="https://img.shields.io/badge/python-3776AB?style=for-the-badge&logo=python&logoColor=white">
      </td>
   </tr>
   <tr>
      <td colspan="2" align="center">
        Library & Framework
      </td>
      <td colspan="4">
        <img src="https://img.shields.io/badge/react-61DAFB?style=for-the-badge&logo=react&logoColor=black">
        <img src="https://img.shields.io/badge/deepl-0f2b46?style=for-the-badge&logo=deepl&logoColor=white">
        <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> 
        <img src="https://img.shields.io/badge/spring data jpa-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> 
        <img src="https://img.shields.io/badge/spring security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white">
        <img src="https://img.shields.io/badge/flask-000000?style=for-the-badge&logo=flask&logoColor=white">
        <img src="https://img.shields.io/badge/pyTorch-ee4c2c?style=for-the-badge&logo=pyTorch&logoColor=black">
        <img src="https://img.shields.io/badge/mui-007fff?style=for-the-badge&logo=mui&logoColor=white">
        <img src="https://img.shields.io/badge/amazon ec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white"> 
        <img src="https://img.shields.io/badge/amazon s3-569A31?style=for-the-badge&logo=amazons3&logoColor=white">
      </td>
   </tr>
   <tr>
      <td colspan="2" align="center">
        Database
      </td>
      <td colspan="4">
        <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
        <img src="https://img.shields.io/badge/amazon rds-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white">
      </td>
   </tr>
   <tr>
      <td colspan="2" align="center">
        Tool
      </td>
      <td colspan="4">
          <img src="https://img.shields.io/badge/intellijidea-000000?style=for-the-badge&logo=intellijidea&logoColor=white">
          <img src="https://img.shields.io/badge/visualstudiocode-007ACC?style=for-the-badge&logo=visualstudiocode&logoColor=white">
      </td>
   </tr>
   <tr>
      <td colspan="2" align="center">
        etc.
      </td>
      <td colspan="4">
          <img src="https://img.shields.io/badge/figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white">
          <img src="https://img.shields.io/badge/swagger-85ea2d?style=for-the-badge&logo=swagger&logoColor=black">
      </td>
   </tr>
</table>
</div>
<br>

## 시스템 구조도
<img width="800" alt="image" src="https://github.com/JongTKim/Algorithm/assets/95161602/300ee33d-1e7b-4db5-8f97-b978556df9dc">
<br>

## 📸 주요 화면

<details>
  <summary>주요 화면 보기</summary>

- 메인 페이지 <br>
  <img width="800" alt="메인 페이지" src="https://github.com/JongTKim/Algorithm/assets/95161602/b3746187-c42b-4a7c-8ac3-a5ae2f924b9b">

- 회원 가입 <br>
 <img width="800" alt="회원 가입" src="https://github.com/JongTKim/Algorithm/assets/95161602/3bfd245b-d9fa-47e9-93ab-1a023dbbe79c">
 
- 친구 추천<br>
  <img width="800" alt="친구 추천" src="https://github.com/JongTKim/Algorithm/assets/95161602/ca531863-d2d4-4980-bc75-e744593a426e">

- 스터디<br>  
  <img width="800" alt="친구 추천" src="https://github.com/JongTKim/Algorithm/assets/95161602/b2c065dc-6b38-487e-9d38-a6cb3be64113">

- 채팅<br>
  <img width="800" alt="채팅" src="https://github.com/JongTKim/Algorithm/assets/95161602/8ee0c746-7900-4078-9d49-4f8204b0b31e">

- 랜덤 채팅 <br>
  <img width="800" alt="채팅" src="https://github.com/JongTKim/Algorithm/assets/95161602/0d0e2dd6-8c4b-470c-b719-af174295f614">

- 다국어 지원 <br>
  <img width="800" alt="랜덤 채팅" src="https://github.com/JongTKim/Algorithm/assets/95161602/95fe66ad-54b4-4f78-b49a-28edc91af25c">


</details>
<br>

## 👩‍👩‍👧‍👦 팀 정보

<div sytle="overflow:hidden;">
<table>
   <tr>
      <td colspan="3" align="center"><strong>Front-End Developer</strong></td>
      <td colspan="1" align="center"><strong>Back-End Developer</strong></td>
   </tr>
  <tr>
    <td align="center">
    <a href="https://github.com/junghada"><img src="https://avatars.githubusercontent.com/u/137380701?v=4" width="150px;" alt="박정하"/><br /><sub><b>박정하</b></sub></a><br />
    </td>
     <td align="center">
        <a href="https://github.com/Jieun6"><img src="https://avatars.githubusercontent.com/u/128337393?v=4" width="150px" alt="김지은"/><br /><sub><b>김지은</b></sub></a>
     </td>
     <td align="center">
        <a href="https://github.com/Noah-JuYoung"><img src="https://avatars.githubusercontent.com/u/103828756?v=4" width="150px" alt="박주용"/><br /><sub><b>박주용</b></sub></a>
     </td>
     <td align="center">
        <a href="https://github.com/JongTKim"><img src="https://avatars.githubusercontent.com/u/95161602?v=4" width="150px" alt="김종태"/><br /><sub><b>김종태</b></sub></a>
     </td>
  <tr>

</table>
</div>

## 판넬
<img width="800" src="https://github.com/JongTKim/Algorithm/assets/95161602/e114ced4-d3a4-40eb-9d86-f11ad23ff033">
