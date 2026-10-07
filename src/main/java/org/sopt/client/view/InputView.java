package org.sopt.client.view;

import java.util.Scanner;

public class InputView {
    private final Scanner scanner = new Scanner(System.in);

    public int readCommand() {
        System.out.print("선택: ");
        return Integer.parseInt(scanner.nextLine());
    }

    public long readPostId(String message) {
        System.out.print(message);
        return Long.parseLong(scanner.nextLine());
    }

    public String readTitle() {
        System.out.print("제목: ");
        return scanner.nextLine();
    }

    public String readContent() {
        System.out.print("내용: ");
        return scanner.nextLine();
    }

    public String readTag() {
        System.out.print("태그 (없으면 Enter): ");
        return scanner.nextLine();
    }

    public int readCategory() {
        System.out.println("1. 자유 / 2. 질문 / 3. 정보");
        System.out.print("카테고리(숫자로 입력): ");
        return Integer.parseInt(scanner.nextLine());
    }
}
