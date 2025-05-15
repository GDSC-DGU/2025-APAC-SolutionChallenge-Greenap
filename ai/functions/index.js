// v2 HTTPS 함수 사용
const { onRequest } = require("firebase-functions/v2/https");
const { logger } = require("firebase-functions");
const axios = require("axios");

const runtimeOpts = {
    memory: "1GiB",
    timeoutSeconds: 60,
    secrets: ["GEMINI_API_KEY"],
};

exports.verifyChallengeImage = onRequest(
    {
        region: "asia-northeast3",
        memory:  "1GiB",
        timeoutSeconds: 60,
        environment: {
            GEMINI_API_KEY: process.env.GEMINI_API_KEY,
        },
    },
    async (req, res) => {
        if (req.method !== "POST")
          return res.status(405).send("Only POST allowed");
    
        const { image_url, challenge_title, challenge_description } = req.body;
        if (!image_url || !challenge_title || !challenge_description) {
          return res
            .status(400)
            .send("Missing fields: image_url, challenge_title, challenge_description");
        }
        try {
            // 1) 이미지 다운로드 → base64로 인코딩
            const imgResp = await axios.get(image_url, { responseType: "arraybuffer" });
            const mimeType = imgResp.headers["content-type"] || "image/jpeg";
            const base64Image = Buffer.from(imgResp.data, "binary").toString("base64");

            // 2) 프롬프트와 이미지 데이터를 하나의 Content로 묶기
            const promptText = `함께 제공되는 이미지가 챌린지 "${challenge_title}" 인증 사진인지 확인해줘: ${challenge_description}\n###Return Only: success | failure`;
            const content = {
                parts: [
                    { text: promptText },
                    {
                        inlineData: {
                            mimeType: mimeType,
                            data: base64Image,
                        },
                    }
                ],
            };

            // 3) API 키 확인
            const apiKey = process.env.GEMINI_API_KEY;
            if (!apiKey) {
                console.error("GEMINI_API_KEY 환경 변수가 설정되지 않았습니다.");
                return res.status(500).json({ success: false, error: "API key configuration error" });
            }

            // 4) API 호출
            const url = `https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro:generateContent?key=${apiKey}`;
            
            const apiRes = await axios.post(url, {
                contents: [content],
                generationConfig: {
                    temperature: 0,
                    maxOutputTokens: 2048,
                }
            });

            if (apiRes.data && apiRes.data.candidates && apiRes.data.candidates.length > 0) {
                const resultText = apiRes.data.candidates[0].content.parts[0].text.trim();

                if (resultText === "success") {
                    return res.json({
                        success: true,
                        status_code: 200,
                        result_text: resultText,
                        message: "인증에 성공했습니다."
                    });
                } else if (resultText === "failure") {
                    return res.json({
                        success: false,
                        status_code: 200,
                        result_text: resultText,
                        message: "인증에 실패했습니다."
                    });
                } else {
                    return res.status(500).json({
                        success: false,
                        status_code: 500,
                        result_text: resultText,
                        message: "모델이 이해할 수 없는 응답을 반환했습니다."
                    });
                }
                //return res.json({ success: true, data: apiRes.data });
            } else {
                console.error("Gemini API returned unexpected response structure:", JSON.stringify(apiRes.data));
                return res.status(500).json({ 
                    success: false, 
                    error: "Invalid API response structure",
                    response: apiRes.data
                });
            }
        } catch (e) {
            console.error("Error in verifyChallengeImage:", e);
            return res.status(500).json({ 
                success: false, 
                error: e.message,
                stack: e.stack
            });
        }
    }
);

// 1) 챌린지 이름+진행률 -> 긍정적 임팩트 메시지
exports.generateChallengeReport = onRequest(
    {
        region: "asia-northeast3",
        memory:  "1GiB",
        timeoutSeconds: 60,
        environment: {
            GEMINI_API_KEY: process.env.GEMINI_API_KEY,
        },
    },
    async (req, res) => {
        if (req.method !== "POST") {
            res.status(405).send("Only POST requests are allowed");
            return;
        }

        const {challenge_title, progress, total_day} = req.body;
        if (challenge_title == null || progress == null || total_day == null) {
            return res.status(400).send("Missing required fields: challenge_title, progress, total_day");
        }

        const apiKey = process.env.GEMINI_API_KEY;
        const model = "gemini-1.5-pro";
        const url = `https://generativelanguage.googleapis.com/v1beta/models/${model}:generateContent?key=${apiKey}`;

        const promptText = `챌린지 "${challenge_title}"의 달성도는 ${progress}이다. 이 달성률로 환경에 어떤 긍정적 영향을 주는지, 재미나고 기쁨과 성취감을 주는 톤으로 한 문장으로 작성해줘.`
        try {
            const apiRes = await axios.post(url, {
                contents: [{parts: [{text: promptText}]}],
                generationConfig: {
                    temperature: 0.7,
                    maxOutputTokens: 2048
                }
            });
            if (apiRes.data && apiRes.data.candidates && apiRes.data.candidates.length > 0) {
                const resultText = apiRes.data.candidates[0].content.parts[0].text.trim();
                return res.json({
                    success: true,
                    status_code: 200,
                    result_text: resultText,
                    message: "요청에 성공했습니다."
                })
            } else {
                return res.status(500).json({
                    success: false,
                    status_code: 500,
                    error: e.message,
                });
            }
            //return res.json({success:true, data: apiRes.data});
        } catch (e) {
            return res.status(500).json({
                success: false,
                status_code: 500,
                error: e.message,
            });
        }
    }
);

exports.encourageChallenge = onRequest(
    {
        region: "asia-northeast3",
        memory:  "1GiB",
        timeoutSeconds: 60,
        environment: {
            GEMINI_API_KEY: process.env.GEMINI_API_KEY,
        },
    },
    async (req, res) => {
        if (req.method !== "POST") {
            res.status(405).send("Only POST requests are allowed");
            return;
        }

        const {user_id, challenge_title, progress} = req.body;
        if (!user_id || !challenge_title || progress === undefined) {
            return res.status(400).send("Missing required fields: user_id, challenge_title, progress");
        }

        const apiKey = process.env.GEMINI_API_KEY;
        const model = "gemini-1.5-pro";
        const url = `https://generativelanguage.googleapis.com/v1beta/models/${model}:generateContent?key=${apiKey}`;
        const promptText = `사용자 ${user_id}가 참여 중인 챌린지: ${challenge_title}가 있어. 이 사용자의 참여율은 ${progress}야. 이 사용자에게 동기 부여가 되는 재미있고 따듯한 독려 메시지를 한 문장으로 보내줘.`;

        try {
            const apiRes = await axios.post(url, {
                contents: [{parts: [{text: promptText}]}],
                generationConfig: {
                    temperature: 0.5,
                    maxOutputTokens: 2048
                }
            });
            if (apiRes.data && apiRes.data.candidates && apiRes.data.candidates.length > 0) {
                const resultText = apiRes.data.candidates[0].content.parts[0].text.trim();
                return res.json({
                    success: true,
                    status_code: 200,
                    result_text: resultText,
                    message: "요청에 성공했습니다."
                })
            } else {
                return res.status(500).json({
                    success: false,
                    status_code: 500,
                    error: e.message,
                });
            }
            //return res.json({success:true, data: apiRes.data});
        } catch (e) {
            return res.status(500).json({
                success: false,
                status_code: 500,
                error: e.message,
            });
        }
    }
);