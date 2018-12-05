import React, { Component } from 'react';
import {
  Container, Row, Col, Card, CardBody, CardTitle, CardImg, Modal, ModalHeader,
  ModalFooter, ModalBody, Button, Form, FormGroup, Label, Input, Table
} from 'reactstrap';
import { ToastContainer, toast } from 'react-toastify';
import { CsvToHtmlTable } from 'react-csv-to-table';
import 'react-toastify/dist/ReactToastify.css';
import './App.css';
import Ionicon from 'react-ionicons'
import loader from './img/load.gif'

import { generateRandomVideo, generateVideo, getGIF, getMP4, analyzeDurations, getVariants, getModel, getThumbnail, getVariantsCSV } from './restApiCalls';

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      scale: "",
      fps: "",
      videoName: "",
      videoPreviewVisible: false,
      videoPreviewModalVisible: false,
      preview: "",
      video: "",
      durationsVisible: false,
      durations: null,
      variantsTabVisible: false,
      variantsCSV: "",
      loadAnalysisVisible: false,
      configuratorVisible: false,
      loadConfiguratorVisible: false,
      model: null,
      modelLoaded: false,
      selected: {}
    };
  }

  toggleVideoPreview() {
    this.setState({
      videoPreviewVisible: !this.state.videoPreviewVisible
    });
  }

  togglevideoPreviewModalVisible() {
    this.setState({
      videoPreviewModalVisible: !this.state.videoPreviewModalVisible
    });
  }

  toggleVariantsTab() {
    this.setState({
      variantsTabVisible: !this.state.variantsTabVisible
    });
  }

  async loadMP4() {
    toast("Load MP4 from server...", {
      position: toast.POSITION.BOTTOM_RIGHT,
      autoClose: 10000
    })
    const response = await getMP4(this.state.videoName);
    const url = URL.createObjectURL(response.data);
    await this.setState({ video: url })
  }

  handleVideoNameChange(name) {
    this.setState({ videoName: name })
  }
  handleFps(fps) {
    this.setState({ fps: fps })
    console.log("fps: '" + this.state.fps + "'");
  }
  handleScale(scale) {
    this.setState({ scale: scale })
  }

  async handleGenerateRandom() {
    this.setState({ videoPreviewModalVisible: true })
    const name = this.state.videoName;
    const fps = this.state.fps;
    const scale = this.state.scale;
    toast("Generate random variant...", {
      position: toast.POSITION.BOTTOM_RIGHT,
      autoClose: 10000
    })
    const response = await generateRandomVideo(name, fps, scale);
    if (response.status === "OK") {
      toast("Random variant generated !", {
        position: toast.POSITION.BOTTOM_RIGHT,
        type: "success",
        autoClose: 10000
      })
      this.getVideoPreview(name)
    } else {
      toast("Random variant generation failed !", {
        position: toast.POSITION.BOTTOM_RIGHT,
        type: "error",
        autoClose: 10000
      })
      console.log(response.message);
    }
  }

  async handleGenerateVideo() {
    this.setState({ videoPreviewModalVisible: true })
    const name = this.state.videoName;
    const fps = this.state.fps;
    const scale = this.state.scale;
    toast("Generate edited video...", {
      position: toast.POSITION.BOTTOM_RIGHT,
      autoClose: 10000
    })
    var clips = {};
    clips['clips'] = this.state.selected;
    const response = await generateVideo(name, fps, scale, clips);
    if (response.status === "OK") {
      toast("Video generated !", {
        position: toast.POSITION.BOTTOM_RIGHT,
        type: "success",
        autoClose: 10000
      })
      this.getVideoPreview(name)
    } else {
      toast("Edited video generation failed !", {
        position: toast.POSITION.BOTTOM_RIGHT,
        type: "error",
        autoClose: 10000
      })
      console.log(response.message);
    }
  }

  async handleAnalyzeDurations() {
    this.setState({
      loadAnalysisVisible: true
    })
    toast("Analyze durations...", {
      position: toast.POSITION.BOTTOM_RIGHT,
      autoClose: 10000
    })
    const durations = await analyzeDurations();
    this.setState({
      durationsVisible: true,
      durations: durations,
      loadAnalysisVisible: false
    })
  }

  async handleAnalyzeSizes() {
    this.setState({
      loadAnalysisVisible: true
    })
    toast("Analyze variants size...", {
      position: toast.POSITION.BOTTOM_RIGHT,
      autoClose: 10000
    })
    const variantsCSV = await getVariants();
    toast("Download CSV file...", {
      position: toast.POSITION.BOTTOM_RIGHT,
      autoClose: 10000
    })
    const response = await getVariantsCSV();
    const url = URL.createObjectURL(response.data);
    this.setState({
      variantsTabVisible: true,
      variantsCSV: variantsCSV,
      csvFile: url,
      loadAnalysisVisible: false
    })
  }

  async handleConfigure() {
    this.setState({
      configuratorVisible: true
    })
    toast("Load Video Model...", {
      position: toast.POSITION.BOTTOM_RIGHT,
      autoClose: 10000
    })
    // load model
    const model = await getModel();
    model.model.map(async (media) => {
      if (media.type === "mandatory" || media.type === "optional") {
        const thumbnail = await getThumbnail(media.id);
        this.setState(prevState => ({
          [media.id]: URL.createObjectURL(thumbnail),
          selected: {
            ...prevState.selected,
            [media.id + '_']: media.id
          }
        }))
      } else {
        const alts = media["alts"];
        const id = media["id"];
        alts.map(async (alt, i) => {
          const thumbnail = await getThumbnail(alt.id);
          this.setState({ [alt.id]: URL.createObjectURL(thumbnail) })
          if (i === 0) {
            this.setState(prevState => ({
              selected: {
                ...prevState.selected,
                [id + '_']: alt.id
              }
            }))
          }
        })
      }
    });
    this.setState({
      model: model.model,
      modelLoaded: true
    })
  }

  async getVideoPreview(name) {
    const response = await getGIF(name);
    const url = URL.createObjectURL(response.data);
    await this.setState({ preview: url })
    console.log(this.state.preview);
    this.toggleVideoPreview();
  }

  toggleOptionalClip(id) {
    if(this.state.selected[id +'_'] === id){
      this.setState(prevState => ({
        selected: {
          ...prevState.selected,
          [id +'_']: ""
        }
      }))
    }else{
      this.setState(prevState => ({
        selected: {
          ...prevState.selected,
          [id +'_']: id
        }
      }))
    }
  }

  toggleAltClip(idAlt, id){
    this.setState(prevState => ({
      selected: {
        ...prevState.selected,
        [idAlt +'_']: id
      }
    }))
  }

  renderVideoPreview() {
    return (
      <Modal isOpen={this.state.videoPreviewModalVisible} toggle={() => this.videoPreviewModalVisible()}>
        <ModalHeader toggle={() => this.togglevideoPreviewModalVisible()}>Video preview</ModalHeader>
        {(this.state.videoPreviewVisible ?
          <div>
            <img top width="100%" src={this.state.preview} alt="video preview" />
            <ModalFooter>
              {(this.state.video === "") ?

                <div>
                  <Button outline color="secondary" onClick={() => this.loadMP4()}>Load MP4</Button>
                </div>
                :
                <div>
                  <a href={this.state.video} download="myVideo.mp4">
                    <Button outline color="primary">Download MP4</Button>
                  </a>{' '}
                </div>
              }
              <a href={this.state.preview} download="myVideoPreview.gif">
                <Button outline color="primary">Download GIF</Button>
              </a>
            </ModalFooter>
          </div>
          :
          <img top width="100%" src={loader} alt="loader" />
        )}

      </Modal>
    )
  }

  renderVariantsTab() {
    return (
      <Modal size="lg" isOpen={this.state.variantsTabVisible} toggle={() => this.toggleVariantsTab()}>
        <ModalHeader toggle={() => this.toggleVariantsTab()}>All Variants</ModalHeader>
        <ModalBody>
          <CsvToHtmlTable
            data={this.state.variantsCSV}
            csvDelimiter=","
            tableClassName="table table-striped table-hover"
          />
        </ModalBody>
        <ModalFooter>
        <a href={this.state.csvFile} download="variants.csv">
          <Button outline color="primary">Download CSV</Button>
        </a>
        </ModalFooter>
      </Modal>
    )
  }

  renderThumbnails() {
    var medias = [];
    for (var i = 0; i < this.state.model.length; i++) {
      const media = this.state.model[i];
      switch (media.type) {
        case "mandatory":
          medias.push(
            <Card className="card clip-card clip-mandatory">
              <CardBody>
                <CardTitle className="card-title white">{media.id} - Mandatory</CardTitle>
              </CardBody>
              <img className="img-clip" top width="300px" src={this.state[media.id]} />
            </Card>
          );
          break;
        case "optional":
          medias.push(
            <Card className="card clip-card clip-optional">
              <CardBody>
                <CardTitle className="card-title white">{media.id} - Optional</CardTitle>
              </CardBody>
              <div className={(this.state.selected[media.id + '_'] === media.id ? "icon" : "icon-disabled")} onClick={() => this.toggleOptionalClip(media.id)}><img className="img-clip" top width="300px" src={this.state[media.id]} /></div>
            </Card >
          );
          break;
        case "alternative":
          const alts = media.alts;
          const id = media.id
          var altblock = []
          var ids = "";
          for (var j = 0; j < alts.length; j++) {
            const altMedia = alts[j];
            var cl = "img-clip";
            if (j === 0) {
              cl = "";
              ids = altMedia.id;
            } else {
              if (j === alts.length - 1) {
                cl = "img-clip";
              }
              ids = ids + ", " + altMedia.id;
            }
            altblock.push(
              <div className={(this.state.selected[id + '_'] === altMedia.id ? "icon" : "icon-disabled")}>
                <img className={cl} top width="300px" src={this.state[altMedia.id]} onClick={()=> this.toggleAltClip(id, altMedia.id)}/>
              </div>
            );
          }
          medias.push(
            <Card className="card clip-card clip-alt">
              <CardBody>
                <CardTitle className="card-title white">{ids} - Alternatives</CardTitle>
              </CardBody>
              {altblock}
            </Card>
          )
          break;
      }
    }
    console.log(medias);
    return medias;
  }

  render() {
    console.log(JSON.stringify(this.state, null, 4));
    return (
      <Container fluid>
        <Row>
          <Col xs="6">
            <Card className="text-center top-cards">
              <CardBody className="text-center">
                <h2>Video Generator</h2>
                <CardTitle>Edit your video or generate a random variant !</CardTitle>
                <Form>
                  <FormGroup>
                    <Label for="videoName">Video name</Label>
                    <Input type="text" id="videoName" onChange={(e) => this.handleVideoNameChange(e.target.value)} required />
                  </FormGroup>
                  {(this.state.modelLoaded ? 
                  <Button className="btn-hover color-11" disabled={(this.state.videoName === "" || this.state.scale === "" || this.state.fps === "" || this.state.scale < 5 || this.state.fps < 5) || this.state.scale > 50 || this.state.fps > 10} onClick={() => this.handleGenerateVideo()}>GENERATE VIDEO</Button>
                  :
                  <Button className="btn-hover color-1" onClick={() => this.handleConfigure()}>CONFIGURE</Button>
                    )}
                  {' '}
                  <Button className="btn-hover color-1" disabled={(this.state.videoName === "" || this.state.scale === "" || this.state.fps === "" || this.state.scale < 5 || this.state.fps < 5) || this.state.scale > 50 || this.state.fps > 10} onClick={() => this.handleGenerateRandom()}>GENERATE RANDOMLY</Button>
                </Form>
              </CardBody>
            </Card>
            <ToastContainer />
          </Col>
          <Col xs="3">
            <Card className="card top-cards">
              <CardBody className="card-body">
                <h2 className="text-center">GIF Quality</h2>
                <CardTitle className="text-center">Set up preview quality.</CardTitle>
                <Form className="mt-40">
                  <FormGroup row>
                    <Label for="ips" sm={6}>fps (5-10)</Label>
                    <Col sm={6}>
                      <Input min="5" max="10" type="number" name="ips" id="ips" onChange={(e) => this.handleFps(e.target.value)} />
                    </Col>{' '}
                  </FormGroup>
                  <FormGroup row>
                    <Label for="ips" sm={6}>size (0-50) %</Label>
                    <Col sm={6}>
                      <Input min="5" max="50" type="number" name="size" id="size" onChange={(e) => this.handleScale(e.target.value)} />
                    </Col>
                  </FormGroup>
                </Form>
              </CardBody>
            </Card>
          </Col>
          <Col xs="3">
            <Card className="card text-center top-cards">
              <CardBody className="card-body text-center">
                <h2>Analysis tools</h2>
                <CardTitle>Analyze model.</CardTitle>
                <Button className="btn-hover color-3" onClick={() => this.handleAnalyzeDurations()}>Duration</Button>{' '}
                <Button className="btn-hover color-3" onClick={() => this.handleAnalyzeSizes()}>Variant sizes</Button>{' '}
                {(this.state.loadAnalysisVisible &&
                  <CardImg src={loader} alt="loader"></CardImg>
                )}
                {(this.state.durationsVisible &&
                  <Table style={{ marginTop: 20 + 'px' }} size="sm">
                    <tbody>
                      <tr>
                        <th scope="row">Max duration</th>
                        <td>{this.state.durations.min} s</td>
                      </tr>
                      <tr>
                        <th scope="row">Min Duration</th>
                        <td>{this.state.durations.max} s</td>
                      </tr>
                    </tbody>
                  </Table>
                )}
              </CardBody>
            </Card>
          </Col>
        </Row>
        {(this.state.configuratorVisible &&
          <Row>
            <Col xs="12">
              <Card className="card text-center">
                <CardBody className="card-body text-center">
                  {(this.state.modelLoaded ?
                    <Row className="row overFlowRow">
                      {this.renderThumbnails()}
                    </Row>
                    :
                    <img top width="30%" src={loader} alt="loader" />
                  )}
                </CardBody>
              </Card>
            </Col>
          </Row>
        )}
        {this.renderVideoPreview()}
        {this.renderVariantsTab()}
      </Container>
    )
  }
}

export default App;
