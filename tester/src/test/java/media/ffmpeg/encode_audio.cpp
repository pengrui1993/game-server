/*
 * Copyright (c) 2001 Fabrice Bellard
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 * @file libavcodec encoding audio API usage examples
 * @example encode_audio.c
 *
 * Generate a synthetic audio signal and encode it to an output mp2 file.
 */

extern "C"
{
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

#include <libavcodec/avcodec.h>

#include <libavutil/channel_layout.h>
#include <libavutil/common.h>
#include <libavutil/frame.h>
#include <libavutil/samplefmt.h>
}
#include<fstream>
#include<iostream>
#define info_exit(msg) {av_log(NULL, AV_LOG_ERROR, msg" in " __FILE__":%d", __LINE__); exit(-1);}
#define exit_on_nz(e,msg) if(e)info_exit(msg)
#define exit_on_nil(e,msg) exit_on_nz((!(e)),msg)
#define exit_on_neg(e,msg) if((e)<0)info_exit(msg)
/* check that a given sample format is supported by the encoder */
static int check_sample_fmt(const AVCodec *codec, enum AVSampleFormat sample_fmt)
{
    const enum AVSampleFormat *p = codec->sample_fmts;
    std::cout << "supported sample format:";
    while (*p != AV_SAMPLE_FMT_NONE) {
        std::cout << *p <<" " ;
        if (*p == sample_fmt) return 1;
        p++;
    }
    std::cout << std::endl;
    return 0;
}

/* just pick the highest supported samplerate */
static int select_sample_rate(const AVCodec *codec)
{
    const int *p;
    int best_samplerate = 0;
    if (!codec->supported_samplerates)
        return 44100;
    p = codec->supported_samplerates;
    while (*p){
        if (!best_samplerate || abs(44100 - *p) < abs(44100 - best_samplerate))
            best_samplerate = *p;
        p++;
    }
    return best_samplerate;
}

/* select layout with the highest channel count */
static int select_channel_layout(const AVCodec *codec, AVChannelLayout *dst)
{
    const AVChannelLayout *p, *best_ch_layout;
    int best_nb_channels = 0;
    if (!codec->ch_layouts)
    {
        const AVChannelLayout cl = AV_CHANNEL_LAYOUT_STEREO;
        return av_channel_layout_copy(dst, &cl);
    }
    p = codec->ch_layouts;
    while (p->nb_channels)
    {
        int nb_channels = p->nb_channels;
        if (nb_channels > best_nb_channels)
        {
            best_ch_layout = p;
            best_nb_channels = nb_channels;
        }
        p++;
    }
    return av_channel_layout_copy(dst, best_ch_layout);
}
int main(int argc, char **argv)
{
    const char *filename;
    const AVCodec *codec; AVCodecContext *c = NULL;
    AVFrame *frame; AVPacket *pkt;
    int i, j, k, ret;
    FILE *f;
    uint16_t *samples; float t;
    if (argc <= 1)
    {
        fprintf(stderr, "Usage: %s <output file>\n", argv[0]);
        return 0;
    }
    filename = argv[1];//make 012 && ./012 audio.out && ffplay audio.out
    exit_on_nil(codec = avcodec_find_encoder(AV_CODEC_ID_MP2),"Codec not found\n"); /* find the encoder */
    exit_on_nil(c = avcodec_alloc_context3(codec),"Could not allocate audio codec context\n");
    c->bit_rate = 64000; /* put sample parameters */
    c->sample_fmt = AV_SAMPLE_FMT_S16;//AV_SAMPLE_FMT_S16P
    exit_on_nil(check_sample_fmt(codec, c->sample_fmt),"Encoder does not support sample format");//av_get_sample_fmt_name(c->sample_fmt)
    c->sample_rate = select_sample_rate(codec);  /* select other audio parameters supported by the encoder */
    exit_on_neg(ret = select_channel_layout(codec, &c->ch_layout),"select_channel_layout");
    exit_on_neg (avcodec_open2(c, codec, NULL) ,"Could not open codec");/* open it */
    exit_on_nil(f = fopen(filename, "wb"),"Could not open input file");
    /* packet for holding encoded output */
    exit_on_nil(pkt = av_packet_alloc(),"could not allocate the packet");
    /* frame containing input raw audio */
    exit_on_nil(frame = av_frame_alloc(),"Could not allocate audio frame");
    frame->nb_samples = c->frame_size;
    frame->format = c->sample_fmt;
    exit_on_neg(ret = av_channel_layout_copy(&frame->ch_layout, &c->ch_layout),"av_channel_layout_copy");
    exit_on_neg(ret = av_frame_get_buffer(frame, 0),"Could not allocate audio data buffers"); /* allocate the data buffers */
    /* encode a single tone sound */
    auto fp = [&](AVFrame* frame){
        exit_on_neg(ret = avcodec_send_frame(c, frame),"Error sending the frame to the encoder");
        while(ret>=0){
            ret = avcodec_receive_packet(c, pkt);//dts:-9223372036854775808,pts:-9223372036854775808,ration(0,1),dur:1152
            std::printf("dur:%lld\n",pkt->duration);
            if (ret == AVERROR(EAGAIN) || ret == AVERROR_EOF)return;
            exit_on_neg(ret,"Error encoding audio frame");
            fwrite(pkt->data, 1, pkt->size, f);
            av_packet_unref(pkt);
        }
    };
    for (t = 0,i = 0; i < 200; i++)
    { /* make sure the frame is writable -- makes a copy if the encoder kept a reference internally */
        exit_on_neg(ret = av_frame_make_writable(frame),"av_frame_make_writable");
        samples = (uint16_t *)frame->data[0];
        for (j = 0; j < c->frame_size; j++)
        {
            samples[2 * j] = (int)(sin(t++*2 * M_PI * 440.0 / c->sample_rate) * 10000);
            for (k = 1; k < c->ch_layout.nb_channels; k++)
                samples[2 * j + k] = samples[2 * j];

        }
        fp(frame);
    }
    fp(nullptr);
    fclose(f);
    av_frame_free(&frame);
    av_packet_free(&pkt);
    avcodec_free_context(&c);
    return 0;
}
