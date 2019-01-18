package io.locally.engagesdk.datamodels.campaign

import java.net.URL

class SurveyResponse(val id: Int,
                     val title: String,
                     val resultEndpoint: URL,
                     val surveyQuestions: Array<SurveyQuestion>,
                     val defaultResultEndpoint: URL) {

    enum class SurveyQuestionType(private val value: String) : CharSequence by value {
        TEXT("text"),
        RATING_5("rating5"),
        RATING_10("rating10"),
        YES_NO("yesNO");

        override fun toString() = value
    }

    class SurveyQuestion(val title: String, val type: SurveyQuestionType, val sort: Int, val surveyId: Int)
}