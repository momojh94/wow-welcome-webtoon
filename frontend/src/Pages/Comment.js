import React from "react";
import Header from "../Components/Header";
//버튼
import Button from "@material-ui/core/Button";
import { makeStyles } from "@material-ui/core/styles";
//링크 관련
import Link from "@material-ui/core/Link";
import Typography from "@material-ui/core/Typography";
//테이블 관련
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableContainer from "@material-ui/core/TableContainer";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import Paper from "@material-ui/core/Paper";
import Checkbox from "@material-ui/core/Checkbox";
//paging
import Pagination from "@material-ui/lab/Pagination";
// 토큰 재발급
var ReToken = require("../AuthRoute");

const useStyles = makeStyles((theme) => ({
  menu: {
    "& > *": {
      margin: theme.spacing(5, 0, 0, 8),
    },
  },
  button: {
    "& > *": {
      margin: theme.spacing(1),
    },
  },
  link: {
    "& > *": {
      margin: theme.spacing(5, 0, 4, 14),
    },
  },
  linkMerge: {
    "& > *": {
      marginRight: theme.spacing(3),
    },
  },
  table: {
    width: 1300,
    marginBottom: 50,
  },
  titleField: {
    maxWidth: 100,
  },
  commentField: {
    maxWidth: 600,
  },
  deleteButton: {
    "& > *": {
      marginTop: theme.spacing(2),
      marginLeft: theme.spacing(167),
    },
  },
  paging: {
    "& > *": {
      marginTop: theme.spacing(2),
    },
    marginLeft: theme.spacing(15),
  },
}));

export default function Comment() {
  const [myComments, setMyComments] = React.useState([]);

  React.useEffect(() => {
    loadMyComments(1);
  }, []);

  const classes = useStyles();
  const [checked, setChecked] = React.useState(true);

  const handleChange = (event) => {
    setChecked(event.target.checked);
  };

  const [pageNum, setPageNum] = React.useState("");

  /* 내 댓글 목록 조회 */
  const loadMyComments = (page) => {
    var myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");
    myHeaders.append("Authorization", localStorage.getItem("authorization"));

    var requestOptions = {
      method: "GET",
      headers: myHeaders,
      redirect: "follow",
    };

    fetch("/api/users/comments?page=" + page, requestOptions)
      .then((response) => response.json())
      .then((result) => {
        console.log(result);
        if (result.error_code != null) {
          // TODO: 23 invalid page
          if (result.error_code == 44) {
            ReToken.ReToken();
            return;
          }

          if (result.error_code == 42) {
            // 로그인 필요한 경우
            if (!localStorage.getItem("authorization")) {
              alert("로그인이 필요한 기능입니다, 로그인 페이지로 이동합니다.");
              window.location.href = "/login";
            } else {
              alert("잘못된 접근입니다.");
            }
            return;
          }

          alert(result.message);
          return;
        }

        setMyComments(result.data.comments);
        setPageNum(result.data.total_pages);
      })
      .catch((error) => console.log("error", error));
  };

  const handlePaging = (event, value) => {
    loadMyComments(value);
  };

  /* 댓글 삭제 */
  const deleteComment = (idx) => {
    var myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");
    myHeaders.append("Authorization", localStorage.getItem("authorization"));

    var raw = "";

    var requestOptions = {
      method: "DELETE",
      headers: myHeaders,
      body: raw,
      redirect: "follow",
    };

    console.log("api/comments/" + idx);
    fetch("/api/comments/" + idx, requestOptions)
      .then((response) => response.json())
      .then((result) => {
        console.log(result);
        if (result.error_code != null) {
          if (result.error_code == 44) {
            ReToken.ReToken();
            return;
          }

          if (result.error_code == 42) {
            // 로그인 필요한 경우
            if (!localStorage.getItem("authorization")) {
              alert("로그인이 필요한 기능입니다, 로그인 페이지로 이동합니다.");
              window.location.href = "/login";
            } else {
              alert("잘못된 접근입니다.");
            }
            return;
          }

          alert(result.message);
          return;
        }

        loadMyComments();
      })
      .catch((error) => console.log("error", error));
  };

  return (
    <div>
      <Header />
      <div className={classes.menu}>
        <div className={classes.button}>
          <Button variant="contained" href="/">
            <span style={{ color: "#212121", fontWeight: 520 }}>도전만화</span>
          </Button>
          <Button variant="contained" color="primary" href="/mypage">
            <span style={{ color: "#fafafa", fontWeight: 550 }}>
              마이페이지
            </span>
          </Button>
        </div>
      </div>

      <div className={classes.link}>
        <Typography className={classes.linkMerge}>
          <Link
            href="/mypage"
            color="inherit"
            style={{
              fontWeight: 500,
              fontSize: "18px",
            }}
          >
            내 작품
          </Link>

          <Link
            href="/mypage/comment"
            color="primary"
            style={{
              fontWeight: 700,
              fontSize: "20px",
            }}
          >
            내 댓글
          </Link>
        </Typography>
      </div>

      <div>
        <div className={classes.paging}>
          <Pagination count={pageNum} color="primary" onChange={handlePaging} />
        </div>
        <TableContainer component={Paper} backgroundColor>
          <Table
            className={classes.table}
            aria-label="simple table"
            align="center"
          >
            <TableHead>
              <TableRow>
                <TableCell align="center">이미지</TableCell>
                <TableCell align="center">제목</TableCell>
                <TableCell align="center">회차</TableCell>
                <TableCell align="center">내 댓글</TableCell>
                <TableCell align="center">좋아요</TableCell>
                <TableCell align="center">싫어요</TableCell>
                <TableCell align="center">등록일</TableCell>
                <TableCell align="center">삭제</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {myComments.map((myComment) => (
                <TableRow key={myComment.idx}>
                  <TableCell align="center">
                    <img src={myComment.webtoon_thumbnail} />
                  </TableCell>
                  <TableCell align="center">
                    <div className={classes.titleField}>
                      {myComment.webtoon_title}
                    </div>
                  </TableCell>
                  <TableCell align="center">{myComment.ep_no}화</TableCell>
                  <TableCell align="center">
                    <div className={classes.commentField}>
                      {myComment.content}
                    </div>
                  </TableCell>
                  <TableCell align="center">{myComment.like_count}</TableCell>
                  <TableCell align="center">
                    {myComment.dislike_count}
                  </TableCell>
                  <TableCell align="center">{myComment.created_date}</TableCell>
                  <TableCell align="center">
                    <Button
                      variant="contained"
                      color="primary"
                      onClick={() => deleteComment(myComment.idx)}
                    >
                      삭제
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </div>
    </div>
  );
}
