import React, { Component } from 'react';
import {
  Container, Row, Col, Card, CardHeader, CardBody, CardTitle, Modal, ModalHeader, ModalFooter,
  Button, Form, FormGroup, Label, Input
} from 'reactstrap';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import './App.css';

import { generateRandomVideo, getGIF, getMP4 } from './restApiCalls';

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      videoName: "",
      videoPreviewVisible: false,
      preview: "",
      video: ""
    };
    this.toggle = this.toggle.bind(this);
  }

  toggle() {
    this.setState({
      videoPreviewVisible: !this.state.videoPreviewVisible
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

  async handleGenerateRandom() {
    const name = this.state.videoName;
    toast("Generate random variant...", {
      position: toast.POSITION.BOTTOM_RIGHT,
      autoClose: 10000
    })
    const response = await generateRandomVideo(name);
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

  async getVideoPreview(name) {
    const response = await getGIF(name);
    const url = URL.createObjectURL(response.data);
    await this.setState({ preview: url })
    console.log(this.state.preview);
    this.toggle();
  }

  renderVideoPreview() {
    return (
      <Modal isOpen={this.state.videoPreviewVisible} toggle={this.toggle} className={this.props.className}>
        <ModalHeader toggle={this.toggle}>Video preview</ModalHeader>
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
      </Modal>
    )
  }

  render() {
    return (
      <Container>
        <Row>
          <Col xs="9">
            <Card>
              <CardHeader><h4>Video Generator</h4></CardHeader>
              <CardBody>
                <CardTitle>Configure your video variant.</CardTitle>
                <Form>
                  <FormGroup>
                    <Label for="videoName">Video name</Label>
                    <Input type="text" id="videoName" placeholder="Enter a video name" onChange={(e) => this.handleVideoNameChange(e.target.value)} required />
                  </FormGroup>
                  <Button disabled={this.state.videoName == ""} color="primary">Generate</Button>{' '}
                  <Button disabled={this.state.videoName == ""} onClick={() => this.handleGenerateRandom()} color="warning">Generate random variant</Button>
                </Form>
              </CardBody>
            </Card>
            <ToastContainer />
          </Col>
          <Col xs="3">
            <Card>
              <CardHeader><h4>Analysis tools</h4></CardHeader>
              <CardBody>
                <CardTitle>Analyze model.</CardTitle>
              </CardBody>
            </Card>
          </Col>
        </Row>{this.renderVideoPreview()}
      </Container>
    )
  }
}

export default App;
