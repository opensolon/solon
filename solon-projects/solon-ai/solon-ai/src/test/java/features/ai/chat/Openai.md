
```json
{
    "id": "931809f8-3d1c-44d9-b124-367d05e0f123",
        "object": "chat.completion",
        "created": 1739092483,
        "model": "deepseek-reasoner",
        "choices": [
            {
                "index": 0,
                "message": {
                    "role": "assistant",
                    "content": "Hello! How can I assist you today?",
                    "reasoning_content": "Okay, the user wrote \"hello\". That's pretty straightforward. I should respond in a friendly and welcoming manner. Maybe say \"Hello! How can I assist you today?\" to prompt them to ask for help. Keep it open-ended so they feel comfortable to ask anything. Let me check for any typos and make sure the tone is right. Yep, that sounds good. Send it off."
                },
                "logprobs": null,
                "finish_reason": "stop"
            }
        ],
        "usage": {
        "prompt_tokens": 6,
            "completion_tokens": 92,
            "total_tokens": 98,
            "prompt_tokens_details": {
            "cached_tokens": 0
        },
        "completion_tokens_details": {
            "reasoning_tokens": 81
        },
        "prompt_cache_hit_tokens": 0,
            "prompt_cache_miss_tokens": 6
    },
    "system_fingerprint": "fp_7e73fd9a08"
}
```

```json
{
  "id": "43a31dda-92f6-4cdf-a333-bb399599a824",
  "object": "chat.completion.chunk",
  "created": 1739097227,
  "model": "deepseek-reasoner",
  "system_fingerprint": "fp_7e73fd9a08",
  "choices": [
    {
      "index": 0,
      "delta": {
        "role": "assistant",
        "content": null,
        "reasoning_content": ""
      },
      "logprobs": null,
      "finish_reason": null
    }
  ]
}
```

```json
{
  "id": "6ca35a25-e012-4746-8d29-f9e6d1e5e26d",
  "object": "chat.completion.chunk",
  "created": 1739099247,
  "model": "deepseek-reasoner",
  "system_fingerprint": "fp_7e73fd9a08",
  "choices": [
    {
      "index": 0,
      "delta": {
        "content": "",
        "reasoning_content": null
      },
      "logprobs": null,
      "finish_reason": "stop"
    }
  ],
  "usage": {
    "prompt_tokens": 6,
    "completion_tokens": 82,
    "total_tokens": 88,
    "prompt_tokens_details": {
      "cached_tokens": 0
    },
    "completion_tokens_details": {
      "reasoning_tokens": 71
    },
    "prompt_cache_hit_tokens": 0,
    "prompt_cache_miss_tokens": 6
  }
}
```